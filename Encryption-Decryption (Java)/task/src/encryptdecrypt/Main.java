package encryptdecrypt;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        String mode = "enc"; // Default mode is encryption
        int key = 0;        // Default key is 0
        String data = "";   // Default data is empty string
        String inFile = "";
        String outFile = "";
        String alg = "shift";
        // Parse command-line arguments
        try {
            for (int i = 0; i < args.length; i += 2) {
                if (i + 1 >= args.length) {
                    System.out.println("Error: Missing value for argument " + args[i]);
                    return;
                }
                String argName = args[i];
                String argValue = args[i + 1];

                switch (argName) {
                    case "-mode":
                        mode = argValue;
                        break;
                    case "-key":
                        try {
                            key = Integer.parseInt(argValue);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Key must be an integer");
                            return;
                        }
                        break;
                    case "-data":
                        data = argValue;
                        break;
                    case "-in":
                        inFile = argValue;
                        break;
                    case "-out":
                        outFile = argValue;
                        break;
                    case "-alg":
                        if (!argValue.equals("shift") && !argValue.equals("unicode")) {
                            System.out.println("Error: Invalid algorithm, use 'shift' or 'unicode'");
                            return;
                        }
                        alg = argValue;
                        break;
                    default:
                        System.out.println("Error: Unknown argument " + argName);
                        return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid arguments");
            return;
        }

        // Determine input source: prefer -data over -in, default to empty string
        String inputData;
        if (!data.isEmpty()) {
            inputData = data;
        } else if (!inFile.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                inputData = sb.toString();
            } catch (IOException e) {
                System.out.println("Error: Input file does not exist or cannot be read");
                return;
            }
        } else {
            inputData = "";
        }

        String result;
        if (mode.equals("dec")) {
            result = alg.equals("shift") ? decryptShift(inputData, key) : decryptUnicode(inputData, key);
        } else {
            result = alg.equals("shift") ? encryptShift(inputData, key) : encryptUnicode(inputData, key);
        }

        // Output the result
        if (!outFile.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                writer.write(result);
            } catch (IOException e) {
                System.out.println("Error: Cannot write to output file");
                return;
            }
        } else {
            System.out.print(result);
        }
    }

    public static String encryptUnicode(String message, int key) {
        StringBuilder newStr = new StringBuilder(message);
        for (int i = 0; i < message.length(); i++) {
            int j = message.charAt(i);
            int formula = j + key;
            newStr.setCharAt(i, (char) formula);
        }
        return newStr.toString();

    }

    public static String decryptUnicode(String message, int key) {
        StringBuilder str = new StringBuilder(message);
        for (int i = 0; i < message.length(); i++) {
            int formula = (126 + message.charAt(i) - key) % 126;
            str.setCharAt(i, (char) (formula));
        }
        return str.toString();

    }

    // Shift algorithm: shifts only English letters (A-Z, a-z)
    public static String encryptShift(String message, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) (base + ((c - base + key) % 26));
                // Ensure positive modulo
                if (c < base) {
                    c += 26;
                }
            }
            result.append(c);
        }
        return result.toString();
    }

    public static String decryptShift(String message, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) (base + ((c - base - key + 26) % 26));
                // Ensure positive modulo
                if (c < base) {
                    c += 26;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
}