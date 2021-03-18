import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import proto.PersonOuterClass;
import proto.PersonServiceGrpc;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Main
{
    public static void main(String[] args)
    {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8999)
                .keepAliveWithoutCalls(true)
                .usePlaintext()
                .build();

        PersonServiceGrpc.PersonServiceBlockingStub personStub = PersonServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        boolean keepConnected = true;

        while(keepConnected)
        {
            System.out.println("Selecteaza actiunea:\n1. Introducere utilizator\n2. Deconectare.");

            int iAction = scanner.nextInt();

            switch(iAction)
            {
                case 1:
                {
                    System.out.println("Introduceti numele clientului: ");
                    String firstName = scanner.next();

                    System.out.println("Introduceti prenumele clientului: ");
                    String lastName = scanner.next();

                    System.out.println("Introduceti CNP-ul clientului: ");
                    String CNP = scanner.next();

                    try {
                        PersonOuterClass.PersonSetResponse response = personStub.setPerson(PersonOuterClass.Person.newBuilder()
                                .setFirstName(firstName)
                                .setLastName(lastName)
                                .setCnp(CNP)
                                .build());

                        System.out.println("Clientul " + firstName + " " + lastName + " a fost procesat, varsta: " + response.getAge() + ", sex: " + response.getGender());

                    } catch (StatusRuntimeException e) {
                        System.out.println("Error: " + e);
                    }

                    break;
                }
                default:
                {
                    keepConnected = false;
                    break;
                }
            }
        }

    }
}
