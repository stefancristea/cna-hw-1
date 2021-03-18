package service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import proto.PersonOuterClass;
import proto.PersonServiceGrpc;
import io.grpc.Status;

import java.time.LocalDate;
import java.time.Period;

public class PersonImpl extends PersonServiceGrpc.PersonServiceImplBase {

    enum Gender
    {
        MALE,
        FEMALE
    };

    private Gender getGender(String CNP) throws IllegalArgumentException
    {
        switch (CNP.charAt(0))
        {
            case '1':
            case '3':
            case '5':
                return Gender.MALE;
            case '2':
            case '4':
            case '6':
                return Gender.FEMALE;
            default:
                throw new IllegalArgumentException("Can't determinate gender from provided CNP");
        }
    }

    private int getBirthYear(String CNP)
    {
        switch(CNP.charAt(0))
        {
            case '1':
            case '2':
                return 1900 + Integer.parseInt(CNP.substring(1, 3));
            case '3':
            case '4':
                return 1800 + Integer.parseInt(CNP.substring(1, 3));
            case '5':
            case '6':
                return 2000 + Integer.parseInt(CNP.substring(1, 3));
        }
        return 0;
    }

    private int getBirthMonth(String CNP)
    {
        return Integer.parseInt(CNP.substring(3, 5));
    }

    private int getBirthDay(String CNP)
    {
        return Integer.parseInt(CNP.substring(5, 7));
    }

    @Override
    public void setPerson(PersonOuterClass.Person request, StreamObserver<PersonOuterClass.PersonSetResponse> responseObserver) {

        final String clientCNP = request.getCnp();

        if (clientCNP.length() != 13)
        {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid CNP length");
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        Gender clientGender = null;

        try
        {
            clientGender = getGender(clientCNP);
        }
        catch(IllegalArgumentException e)
        {
            Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        LocalDate birthDate = null;

        try
        {
            birthDate = LocalDate.of(getBirthYear(clientCNP), getBirthMonth(clientCNP), getBirthDay(clientCNP));
        }
        catch(java.time.DateTimeException e)
        {
            Status status = Status.INVALID_ARGUMENT.withDescription("CNP error: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        LocalDate currentDate = LocalDate.now();

        final int clientAge = Period.between(birthDate, currentDate).getYears();

        System.out.println(request.getFirstName() + " " + request.getLastName() + ": " + clientGender + " " + clientAge + " years old.");

        responseObserver.onNext(PersonOuterClass.PersonSetResponse.newBuilder().setAge(clientAge).setGender(clientGender.toString()).build());
        responseObserver.onCompleted();
    }
}
