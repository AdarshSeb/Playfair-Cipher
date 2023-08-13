package com.example.playfair;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private EditText editTextMessage;
    private EditText editTextKey;
    private TextView textViewEncryptedMessage;
    private TextView textViewDecryptedMessage;
    private Button b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        editTextKey = findViewById(R.id.editTextKey);
        textViewEncryptedMessage = findViewById(R.id.textViewEncryptedMessage);
        textViewDecryptedMessage = findViewById(R.id.textViewDecryptedMessage);
    }
    public void encryptMessage(View view) {
        String message = editTextMessage.getText().toString().toUpperCase().replaceAll("[^A-Z]", "");
        String key = editTextKey.getText().toString().toUpperCase().replaceAll("[^A-Z]", "");

        String encryptedMessage = encryptPlayfair(message, key);
        textViewEncryptedMessage.setText(encryptedMessage);

        textViewDecryptedMessage.setText(""); // Clear the decrypted message field

        textViewEncryptedMessage.setVisibility(View.VISIBLE);
        b2 = findViewById(R.id.button2);
        b2.setVisibility(View.VISIBLE);


    }

    public void decryptMessage(View view) {
        String encryptedMessage = textViewEncryptedMessage.getText().toString().toUpperCase().replaceAll("[^A-Z]", "");
        String key = editTextKey.getText().toString().toUpperCase().replaceAll("[^A-Z]", "");

        String decryptedMessage = decryptPlayfair(encryptedMessage, key);
        textViewDecryptedMessage.setText(decryptedMessage);
        textViewDecryptedMessage.setVisibility(View.VISIBLE);
    }

    private String encryptPlayfair(String message, String key) {
        // Prepare the key matrix
        char[][] keyMatrix = prepareKeyMatrix(key);

        // Prepare the message pairs
        List<String> pairs = prepareMessagePairs(message);

        // Encrypt the pairs using the key matrix
        StringBuilder encryptedMessage = new StringBuilder();
        for (String pair : pairs) {
            char firstChar = pair.charAt(0);
            char secondChar = pair.charAt(1);

            int[] firstCharPosition = findCharacterPosition(keyMatrix, firstChar);
            int[] secondCharPosition = findCharacterPosition(keyMatrix, secondChar);

            char encryptedFirstChar, encryptedSecondChar;

            // Characters are in the same row
            if (firstCharPosition[0] == secondCharPosition[0]) {
                encryptedFirstChar = keyMatrix[firstCharPosition[0]][(firstCharPosition[1] + 1) % 5];
                encryptedSecondChar = keyMatrix[secondCharPosition[0]][(secondCharPosition[1] + 1) % 5];
            }
            // Characters are in the same column
            else if (firstCharPosition[1] == secondCharPosition[1]) {
                encryptedFirstChar = keyMatrix[(firstCharPosition[0] + 1) % 5][firstCharPosition[1]];
                encryptedSecondChar = keyMatrix[(secondCharPosition[0] + 1) % 5][secondCharPosition[1]];
            }
            // Characters are in different rows and columns
            else {
                encryptedFirstChar = keyMatrix[firstCharPosition[0]][secondCharPosition[1]];
                encryptedSecondChar = keyMatrix[secondCharPosition[0]][firstCharPosition[1]];
            }

            encryptedMessage.append(encryptedFirstChar).append(encryptedSecondChar);
        }

        return encryptedMessage.toString();
    }

    private String decryptPlayfair(String encryptedMessage, String key) {
        // Prepare the key matrix
        char[][] keyMatrix = prepareKeyMatrix(key);

        // Prepare the encrypted message pairs
        List<String> pairs = prepareMessagePairs(encryptedMessage);

        // Decrypt the pairs using the key matrix
        StringBuilder decryptedMessage = new StringBuilder();
        for (String pair : pairs) {
            char firstChar = pair.charAt(0);
            char secondChar = pair.charAt(1);

            int[] firstCharPosition = findCharacterPosition(keyMatrix, firstChar);
            int[] secondCharPosition = findCharacterPosition(keyMatrix, secondChar);

            char decryptedFirstChar, decryptedSecondChar;

            // Characters are in the same row
            if (firstCharPosition[0] == secondCharPosition[0]) {
                decryptedFirstChar = keyMatrix[firstCharPosition[0]][(firstCharPosition[1] + 4) % 5];
                decryptedSecondChar = keyMatrix[secondCharPosition[0]][(secondCharPosition[1] + 4) % 5];
            }
            // Characters are in the same column
            else if (firstCharPosition[1] == secondCharPosition[1]) {
                decryptedFirstChar = keyMatrix[(firstCharPosition[0] + 4) % 5][firstCharPosition[1]];
                decryptedSecondChar = keyMatrix[(secondCharPosition[0] + 4) % 5][secondCharPosition[1]];
            }
            // Characters are in different rows and columns
            else {
                decryptedFirstChar = keyMatrix[firstCharPosition[0]][secondCharPosition[1]];
                decryptedSecondChar = keyMatrix[secondCharPosition[0]][firstCharPosition[1]];
            }

            decryptedMessage.append(decryptedFirstChar).append(decryptedSecondChar);
        }

        // Remove any trailing 'X' added during encryption
        int lastIndex = decryptedMessage.length() - 1;
        if (decryptedMessage.charAt(lastIndex) == 'X') {
            decryptedMessage.deleteCharAt(lastIndex);
        }

        return decryptedMessage.toString();
    }




    private char[][] prepareKeyMatrix(String key) {
        // Remove duplicates from the key
        StringBuilder keyBuilder = new StringBuilder();
        Set<Character> uniqueChars = new LinkedHashSet<>();
        for (char c : key.toCharArray()) {
            uniqueChars.add(Character.toUpperCase(c));
        }
        for (char c : uniqueChars) {
            keyBuilder.append(c);
        }

        // Create the key matrix
        String keyString = keyBuilder.toString().replaceAll("[J]", "I");
        char[][] keyMatrix = new char[5][5];
        int row = 0, col = 0;
        for (char c : keyString.toCharArray()) {
            keyMatrix[row][col] = c;
            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }

        // Fill the remaining cells with other alphabets
        char currentChar = 'A';
        while (row < 5) {
            while (col < 5) {
                if (!keyString.contains(String.valueOf(currentChar)) && currentChar != 'J') {
                    keyMatrix[row][col] = currentChar;
                    col++;
                }
                currentChar++;
            }
            col = 0;
            row++;
        }

        return keyMatrix;
    }


    private List<String> prepareMessagePairs(String message) {
        // Remove any non-alphabetic characters and convert to uppercase
        String cleanedMessage = message.replaceAll("[^A-Z\\s]", "").toUpperCase();

        // Replace any 'J' with 'I'
        cleanedMessage = cleanedMessage.replaceAll("[J]", "I");

        // Split the message into pairs of characters
        List<String> pairs = new ArrayList<>();
        int index = 0;
        while (index < cleanedMessage.length()) {
            char firstChar = cleanedMessage.charAt(index);
            char secondChar;
            index++;
            if (index < cleanedMessage.length() && cleanedMessage.charAt(index) != firstChar) {
                secondChar = cleanedMessage.charAt(index);
                index++;
            } else {
                secondChar = 'X';
            }
            pairs.add(String.valueOf(firstChar) + String.valueOf(secondChar));
        }

        return pairs;
    }


    private int[] findCharacterPosition(char[][] keyMatrix, char character) {
        int[] position = new int[2];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (keyMatrix[i][j] == character) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return position;
    }

}