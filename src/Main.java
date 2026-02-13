public class Main {
	
	
	
	
    public static void main(String[] args) throws Exception {
        String userInput = args.length > 0 ? args[0] : "echo hello";

        System.out.println("Hello and welcome!");

        // absichtlich unsicher (typischer Trigger)
        Runtime.getRuntime().exec(userInput);
    }
}
