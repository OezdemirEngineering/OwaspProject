//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main(String[] args) {
    // Simulierter "User Input" (für Demo)
    String userInput = args.length > 0 ? args[0] : "echo hello";

    IO.println(String.format("Hello and welcome!"));

    for (int i = 1; i <= 5; i++) {
        IO.println("i = " + i);
    }

    // ❌ Absichtlich unsicher: Command Injection / Dangerous exec
    try {
        Runtime.getRuntime().exec(userInput);
    } catch (Exception e) {
        IO.println("Exec failed: " + e.getMessage());
    }
}
