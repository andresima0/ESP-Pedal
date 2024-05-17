package br.com.local.esp_pedal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MacAddressTextWatcher implements TextWatcher {

    // Chave para salvar o endereço MAC nas preferências compartilhadas
    private static final String MAC_ADDRESS_KEY = "mac_address";

    // Comprimento total do endereço MAC sem os separadores
    public static final int MAC_ADDRESS_LENGTH = 12;
    // Tamanho de cada bloco no endereço MAC
    private static final int MAC_ADDRESS_BLOCK_SIZE = 2;
    // Separador de endereço MAC
    private static final char MAC_ADDRESS_SEPARATOR = ':';

    // Variável de controle para evitar loops infinitos durante a formatação
    private boolean isFormatting;
    // Referência para o EditText que está sendo observado
    private final EditText editText;
    // Referência para SharedPreferences para salvar o endereço MAC
    private final SharedPreferences sharedPreferences;

    // Construtor que recebe o EditText a ser formatado e configura o endereço MAC salvo nele
    public MacAddressTextWatcher(EditText editText) {
        this.editText = editText;
        this.sharedPreferences = editText.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedMacAddress = sharedPreferences.getString(MAC_ADDRESS_KEY, "");
        editText.setText(savedMacAddress);
    }

    // Método para formatar o endereço MAC enquanto o usuário digita
    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) return;
        isFormatting = true;

        StringBuilder formattedText = new StringBuilder();
        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c) && Character.digit(c, 16) != -1) {
                if (count > 0 && count % MAC_ADDRESS_BLOCK_SIZE == 0) {
                    formattedText.append(MAC_ADDRESS_SEPARATOR);
                }
                formattedText.append(Character.toUpperCase(c));
                count++;
            }
            if (count == MAC_ADDRESS_LENGTH) {
                break;
            }
        }

        editText.removeTextChangedListener(this);
        editText.setText(formattedText);
        editText.setSelection(formattedText.length());
        editText.addTextChangedListener(this);

        saveMacAddress(formattedText.toString());

        isFormatting = false;
    }

    // Método para salvar o endereço MAC formatado nas preferências compartilhadas
    private void saveMacAddress(String macAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MAC_ADDRESS_KEY, macAddress);
        editor.apply();
    }

    // Métodos não utilizados, mas necessários para implementar a interface TextWatcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}