package br.com.local.esp_pedal;

// Importação de bibliotecas

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

// Definição da classe MainActivity
public class MainActivity extends AppCompatActivity {

    // Declaração de variáveis para permissão Bluetooth e conexão
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isConnected = false;
    private static final UUID UUID_BT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Declaração de layouts e elementos da interface do usuário
    private View mainLayout;
    private View presetsLayout, preset01Layout, preset02Layout, preset03Layout;

    // Declaração de campo de entrada de texto
    private EditText macAddressEditText;

    // Declaração de botões e estados de saída
    private Button buttonConnect, presetSwitchButton;
    private Button presetButton01, presetButton02, presetButton03, backMainButton;
    private Button buttonOut01, buttonOut02, buttonOut03, buttonOut04, buttonOut05, buttonOut06;
    private Button buttonOut07, buttonOut08, buttonOut09, buttonOut10, buttonOut11, buttonOut12;
    private Button buttonOut13, buttonOut14, buttonOut15, buttonOut16, buttonOut17, buttonOut18;
    private Button buttonOut19, buttonOut20, buttonOut21, buttonOut22, buttonOut23, buttonOut24;
    private Button back01SwitchButton, back02SwitchButton, back03SwitchButton;

    private boolean isOut01_On = false, isOut02_On = false, isOut03_On = false, isOut04_On = false;
    private boolean isOut05_On = false, isOut06_On = false, isOut07_On = false, isOut08_On = false;
    private boolean isOut09_On = false, isOut10_On = false, isOut11_On = false, isOut12_On = false;
    private boolean isOut13_On = false, isOut14_On = false, isOut15_On = false, isOut16_On = false;
    private boolean isOut17_On = false, isOut18_On = false, isOut19_On = false, isOut20_On = false;
    private boolean isOut21_On = false, isOut22_On = false, isOut23_On = false, isOut24_On = false;

    // Handler usado para enviar mensagens entre threads
    private final Handler handler = new Handler(msg -> {
        if (msg.what == 0) {
            String receivedData = (String) msg.obj;
            updateButtonPreset01Color(receivedData);
            updateButtonPreset02Color(receivedData);
            updateButtonPreset03Color(receivedData);
        }
        return true;
    });

    private static final int UPDATE_INTERVAL = 100; // Intervalo de atualização em milissegundos

    private final Handler updateHandler = new Handler();

    // Runnable usado para atualizações periódicas
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            char[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            boolean[] outputs = {
                    isOut01_On, isOut02_On, isOut03_On, isOut04_On, isOut05_On, isOut06_On,
                    isOut07_On, isOut08_On, isOut09_On, isOut10_On, isOut11_On, isOut12_On,
                    isOut13_On, isOut14_On, isOut15_On, isOut16_On, isOut17_On, isOut18_On,
                    isOut19_On, isOut20_On, isOut21_On, isOut22_On, isOut23_On, isOut24_On
            };

            for (int i = 0; i < outputs.length; i++) {
                char data = outputs[i] ? Character.toUpperCase(letters[i]) : letters[i];
                sendBluetoothData(String.valueOf(data));
            }
            updateHandler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    // On create: método chamado quando a atividade é criada
    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflação de layouts
        mainLayout = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        presetsLayout = LayoutInflater.from(this).inflate(R.layout.activity_presets, null);
        preset01Layout = LayoutInflater.from(this).inflate(R.layout.activity_preset01, null);
        preset02Layout = LayoutInflater.from(this).inflate(R.layout.activity_preset02, null);
        preset03Layout = LayoutInflater.from(this).inflate(R.layout.activity_preset03, null);

        // Definição de layout principal
        setContentView(mainLayout);

        // Inicialização de elementos da interface do usuário
        macAddressEditText = mainLayout.findViewById(R.id.macAddressEditText);
        macAddressEditText.addTextChangedListener(new MacAddressTextWatcher(macAddressEditText));
        buttonConnect = mainLayout.findViewById(R.id.buttonConnect);
        presetSwitchButton = mainLayout.findViewById(R.id.presetSwitchButton);
        presetButton01 = presetsLayout.findViewById(R.id.presetButton01);
        presetButton02 = presetsLayout.findViewById(R.id.presetButton02);
        presetButton03 = presetsLayout.findViewById(R.id.presetButton03);
        backMainButton = presetsLayout.findViewById((R.id.backMainButton));
        buttonOut01 = preset01Layout.findViewById(R.id.buttonOut01);
        buttonOut02 = preset01Layout.findViewById(R.id.buttonOut02);
        buttonOut03 = preset01Layout.findViewById(R.id.buttonOut03);
        buttonOut04 = preset01Layout.findViewById(R.id.buttonOut04);
        buttonOut05 = preset01Layout.findViewById(R.id.buttonOut05);
        buttonOut06 = preset01Layout.findViewById(R.id.buttonOut06);
        buttonOut07 = preset01Layout.findViewById(R.id.buttonOut07);
        buttonOut08 = preset01Layout.findViewById(R.id.buttonOut08);
        buttonOut09 = preset02Layout.findViewById(R.id.buttonOut09);
        buttonOut10 = preset02Layout.findViewById(R.id.buttonOut10);
        buttonOut11 = preset02Layout.findViewById(R.id.buttonOut11);
        buttonOut12 = preset02Layout.findViewById(R.id.buttonOut12);
        buttonOut13 = preset02Layout.findViewById(R.id.buttonOut13);
        buttonOut14 = preset02Layout.findViewById(R.id.buttonOut14);
        buttonOut15 = preset02Layout.findViewById(R.id.buttonOut15);
        buttonOut16 = preset02Layout.findViewById(R.id.buttonOut16);
        buttonOut17 = preset03Layout.findViewById(R.id.buttonOut17);
        buttonOut18 = preset03Layout.findViewById(R.id.buttonOut18);
        buttonOut19 = preset03Layout.findViewById(R.id.buttonOut19);
        buttonOut20 = preset03Layout.findViewById(R.id.buttonOut20);
        buttonOut21 = preset03Layout.findViewById(R.id.buttonOut21);
        buttonOut22 = preset03Layout.findViewById(R.id.buttonOut22);
        buttonOut23 = preset03Layout.findViewById(R.id.buttonOut23);
        buttonOut24 = preset03Layout.findViewById(R.id.buttonOut24);
        back01SwitchButton = preset01Layout.findViewById(R.id.back01SwitchButton);
        back02SwitchButton = preset02Layout.findViewById(R.id.back02SwitchButton);
        back03SwitchButton = preset03Layout.findViewById(R.id.back03SwitchButton);

        // Carregamento de estado dos botões salvos no SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ButtonState", Context.MODE_PRIVATE);
        isOut01_On = sharedPreferences.getBoolean("isOut01_On", false);
        isOut02_On = sharedPreferences.getBoolean("isOut02_On", false);
        isOut03_On = sharedPreferences.getBoolean("isOut03_On", false);
        isOut04_On = sharedPreferences.getBoolean("isOut04_On", false);
        isOut05_On = sharedPreferences.getBoolean("isOut05_On", false);
        isOut06_On = sharedPreferences.getBoolean("isOut06_On", false);
        isOut07_On = sharedPreferences.getBoolean("isOut07_On", false);
        isOut08_On = sharedPreferences.getBoolean("isOut08_On", false);
        isOut09_On = sharedPreferences.getBoolean("isOut09_On", false);
        isOut10_On = sharedPreferences.getBoolean("isOut10_On", false);
        isOut11_On = sharedPreferences.getBoolean("isOut11_On", false);
        isOut12_On = sharedPreferences.getBoolean("isOut12_On", false);
        isOut13_On = sharedPreferences.getBoolean("isOut13_On", false);
        isOut14_On = sharedPreferences.getBoolean("isOut14_On", false);
        isOut15_On = sharedPreferences.getBoolean("isOut15_On", false);
        isOut16_On = sharedPreferences.getBoolean("isOut16_On", false);
        isOut17_On = sharedPreferences.getBoolean("isOut17_On", false);
        isOut18_On = sharedPreferences.getBoolean("isOut18_On", false);
        isOut19_On = sharedPreferences.getBoolean("isOut19_On", false);
        isOut20_On = sharedPreferences.getBoolean("isOut20_On", false);
        isOut21_On = sharedPreferences.getBoolean("isOut21_On", false);
        isOut22_On = sharedPreferences.getBoolean("isOut22_On", false);
        isOut23_On = sharedPreferences.getBoolean("isOut23_On", false);
        isOut24_On = sharedPreferences.getBoolean("isOut24_On", false);

        // Verificação de permissões Bluetooth e inicialização Bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSION);
        } else {
            initializeBluetooth();
        }

        // Configuração de click listeners para os botões
        buttonConnect.setOnClickListener(v -> {
            if (!isConnected) {
                connectToBluetooth();
            } else {
                disconnectBluetooth();
            }
        });

        presetSwitchButton.setOnClickListener(v -> {
            if (mainLayout.getParent() != null) {
                setContentView(presetsLayout);
            } else {
                setContentView(mainLayout);
            }
        });

        presetButton01.setOnClickListener(v -> {
            if (presetsLayout.getParent() != null) {
                setContentView(preset01Layout);
            } else {
                setContentView(presetsLayout);
            }
        });

        presetButton02.setOnClickListener(v -> {
            if (presetsLayout.getParent() != null) {
                setContentView(preset02Layout);
            } else {
                setContentView(presetsLayout);
            }
        });

        presetButton03.setOnClickListener(v -> {
            if (presetsLayout.getParent() != null) {
                setContentView(preset03Layout);
            } else {
                setContentView(presetsLayout);
            }
        });

        backMainButton.setOnClickListener(v -> {
            if (presetsLayout.getParent() != null) {
                setContentView(mainLayout);
            } else {
                setContentView(presetsLayout);
            }
        });

        buttonOut01.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut01_On = !isOut01_On) ? "a" : "A");
                updateButtonOut01Color();
            }
        });

        buttonOut02.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut02_On = !isOut02_On) ? "b" : "B");
                updateButtonOut02Color();
            }
        });

        buttonOut03.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut03_On = !isOut03_On) ? "c" : "C");
                updateButtonOut03Color();
            }
        });

        buttonOut04.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut04_On = !isOut04_On) ? "d" : "D");
                updateButtonOut04Color();
            }
        });

        buttonOut05.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut05_On = !isOut05_On) ? "e" : "E");
                updateButtonOut05Color();
            }
        });

        buttonOut06.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut06_On = !isOut06_On) ? "f" : "F");
                updateButtonOut06Color();
            }
        });

        buttonOut07.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut07_On = !isOut07_On) ? "g" : "G");
                updateButtonOut07Color();
            }
        });

        buttonOut08.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut08_On = !isOut08_On) ? "h" : "H");
                updateButtonOut08Color();
            }
        });

        buttonOut09.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut09_On = !isOut09_On) ? "i" : "I");
                updateButtonOut09Color();
            }
        });

        buttonOut10.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut10_On = !isOut10_On) ? "j" : "J");
                updateButtonOut10Color();
            }
        });

        buttonOut11.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut11_On = !isOut11_On) ? "k" : "K");
                updateButtonOut11Color();
            }
        });

        buttonOut12.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut12_On = !isOut12_On) ? "l" : "L");
                updateButtonOut12Color();
            }
        });

        buttonOut13.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut13_On = !isOut13_On) ? "m" : "M");
                updateButtonOut13Color();
            }
        });

        buttonOut14.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut14_On = !isOut14_On) ? "n" : "N");
                updateButtonOut14Color();
            }
        });

        buttonOut15.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut15_On = !isOut15_On) ? "o" : "O");
                updateButtonOut15Color();
            }
        });

        buttonOut16.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut16_On = !isOut16_On) ? "p" : "P");
                updateButtonOut16Color();
            }
        });

        buttonOut17.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut17_On = !isOut17_On) ? "q" : "Q");
                updateButtonOut17Color();
            }
        });

        buttonOut18.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut18_On = !isOut18_On) ? "r" : "R");
                updateButtonOut18Color();
            }
        });

        buttonOut19.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut19_On = !isOut19_On) ? "s" : "S");
                updateButtonOut19Color();
            }
        });

        buttonOut20.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut20_On = !isOut20_On) ? "t" : "T");
                updateButtonOut20Color();
            }
        });

        buttonOut21.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut21_On = !isOut21_On) ? "u" : "U");
                updateButtonOut21Color();
            }
        });

        buttonOut22.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut22_On = !isOut22_On) ? "v" : "V");
                updateButtonOut22Color();
            }
        });

        buttonOut23.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut23_On = !isOut23_On) ? "w" : "W");
                updateButtonOut23Color();
            }
        });

        buttonOut24.setOnClickListener(v -> {
            if (isConnected) {
                sendBluetoothData((isOut24_On = !isOut24_On) ? "x" : "X");
                updateButtonOut24Color();
            }
        });

        back01SwitchButton.setOnClickListener(v -> {
            if (preset01Layout.getParent() != null) {
                setContentView(presetsLayout);
            } else {
                setContentView(preset01Layout);
            }
        });


        back02SwitchButton.setOnClickListener(v -> {
            if (preset02Layout.getParent() != null) {
                setContentView(presetsLayout);
            } else {
                setContentView(preset02Layout);
            }
        });


        back03SwitchButton.setOnClickListener(v -> {
            if (preset03Layout.getParent() != null) {
                setContentView(presetsLayout);
            } else {
                setContentView(preset03Layout);
            }
        });

        // Inicialização das atualizações periódicas dos botões
        startButtonUpdates();

        // Atualização das cores dos botões de preset
        updateButtonPreset01Color("0");
        updateButtonPreset02Color("0");
        updateButtonPreset03Color("0");
    }

    // Método para salvar o estado de um botão no SharedPreferences
    private void saveButtonState(String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences("ButtonState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Métodos para atualizar as cores dos botões de saída
    private void updateButtonColor(Button button, boolean isOn, int colorResourceId, String stateKey) {
        int color = isOn ? ContextCompat.getColor(this, colorResourceId) :
                ContextCompat.getColor(this, R.color.gray);
        button.setBackgroundColor(color);
        saveButtonState(stateKey, isOn);
    }

    private void updateButtonOut01Color() {
        updateButtonColor(buttonOut01, isOut01_On, R.color.red, "isOut01_On");
    }

    private void updateButtonOut02Color() {
        updateButtonColor(buttonOut02, isOut02_On, R.color.red, "isOut02_On");
    }

    private void updateButtonOut03Color() {
        updateButtonColor(buttonOut03, isOut03_On, R.color.red, "isOut03_On");
    }

    private void updateButtonOut04Color() {
        updateButtonColor(buttonOut04, isOut04_On, R.color.red, "isOut04_On");
    }

    private void updateButtonOut05Color() {
        updateButtonColor(buttonOut05, isOut05_On, R.color.red, "isOut05_On");
    }

    private void updateButtonOut06Color() {
        updateButtonColor(buttonOut06, isOut06_On, R.color.red, "isOut06_On");
    }

    private void updateButtonOut07Color() {
        updateButtonColor(buttonOut07, isOut07_On, R.color.red, "isOut07_On");
    }

    private void updateButtonOut08Color() {
        updateButtonColor(buttonOut08, isOut08_On, R.color.red, "isOut08_On");
    }

    private void updateButtonOut09Color() {
        updateButtonColor(buttonOut09, isOut09_On, R.color.green, "isOut09_On");
    }

    private void updateButtonOut10Color() {
        updateButtonColor(buttonOut10, isOut10_On, R.color.green, "isOut10_On");
    }

    private void updateButtonOut11Color() {
        updateButtonColor(buttonOut11, isOut11_On, R.color.green, "isOut11_On");
    }

    private void updateButtonOut12Color() {
        updateButtonColor(buttonOut12, isOut12_On, R.color.green, "isOut12_On");
    }

    private void updateButtonOut13Color() {
        updateButtonColor(buttonOut13, isOut13_On, R.color.green, "isOut13_On");
    }

    private void updateButtonOut14Color() {
        updateButtonColor(buttonOut14, isOut14_On, R.color.green, "isOut14_On");
    }

    private void updateButtonOut15Color() {
        updateButtonColor(buttonOut15, isOut15_On, R.color.green, "isOut15_On");
    }

    private void updateButtonOut16Color() {
        updateButtonColor(buttonOut16, isOut16_On, R.color.green, "isOut16_On");
    }

    private void updateButtonOut17Color() {
        updateButtonColor(buttonOut17, isOut17_On, R.color.blue, "isOut17_On");
    }

    private void updateButtonOut18Color() {
        updateButtonColor(buttonOut18, isOut18_On, R.color.blue, "isOut18_On");
    }

    private void updateButtonOut19Color() {
        updateButtonColor(buttonOut19, isOut19_On, R.color.blue, "isOut19_On");
    }

    private void updateButtonOut20Color() {
        updateButtonColor(buttonOut20, isOut20_On, R.color.blue, "isOut20_On");
    }

    private void updateButtonOut21Color() {
        updateButtonColor(buttonOut21, isOut21_On, R.color.blue, "isOut21_On");
    }

    private void updateButtonOut22Color() {
        updateButtonColor(buttonOut22, isOut22_On, R.color.blue, "isOut22_On");
    }

    private void updateButtonOut23Color() {
        updateButtonColor(buttonOut23, isOut23_On, R.color.blue, "isOut23_On");
    }

    private void updateButtonOut24Color() {
        updateButtonColor(buttonOut24, isOut24_On, R.color.blue, "isOut24_On");
    }

    // Métodos para atualizar as cores dos botões de preset
    private void updateButtonPreset01Color(String receivedData) {
        int color = receivedData.equals("1") ? ContextCompat.getColor(this, R.color.red) :
                ContextCompat.getColor(this, R.color.gray);
        presetButton01.setBackgroundColor(color);
    }

    private void updateButtonPreset02Color(String receivedData) {
        int color = receivedData.equals("2") ? ContextCompat.getColor(this, R.color.green) :
                ContextCompat.getColor(this, R.color.gray);
        presetButton02.setBackgroundColor(color);
    }

    private void updateButtonPreset03Color(String receivedData) {
        int color = receivedData.equals("3") ? ContextCompat.getColor(this, R.color.blue) :
                ContextCompat.getColor(this, R.color.gray);
        presetButton03.setBackgroundColor(color);
    }

    // Método para exibir um Toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Método para inicializar o Bluetooth
    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported on this device.");
            finish();
        }
    }

    // Método para conectar ao dispositivo Bluetooth
    @SuppressLint("SetTextI18n")
    private void connectToBluetooth() {
        String macAddress = macAddressEditText.getText().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(macAddressEditText.getWindowToken(), 0);

        if (macAddress.isEmpty()) {
            showToast("Please enter a MAC address.");
            return;
        }

        new Handler().postDelayed(() -> {
            if (macAddressEditText.getText().toString().length() < MacAddressTextWatcher.MAC_ADDRESS_LENGTH) {
                showToast("Invalid MAC Address.");
                return;
            }

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID_BT);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
                isConnected = true;

                showToast("Connected to ESP Pedal.");
                buttonConnect.setText("Disconnect");
                enableControlButtons(true);

                startListening();

            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to connect to ESP Pedal.");
            } catch (SecurityException e) {
                e.printStackTrace();
                showToast("Bluetooth permission not granted.");
            }
        }, 200);
    }

    // Método para desconectar ao dispositivo Bluetooth
    @SuppressLint("SetTextI18n")
    private void disconnectBluetooth() {
        try {
            bluetoothSocket.close();
            isConnected = false;

            showToast("Disconnected from ESP Pedal.");
            buttonConnect.setText("Connect");
            enableControlButtons(false);
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error disconnecting from ESP Pedal.");
        }
    }

    // Método para enviar dados via Bluetooth
    private void sendBluetoothData(String data) {
        if (isConnected) {
            try {
                outputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error sending data to ESP Pedal.");
            }
        }
    }

    /// Método para iniciar a escuta de dados Bluetooth
    private void startListening() {
        Thread thread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String receivedData = new String(buffer, 0, bytes);
                    handler.obtainMessage(0, bytes, -1, receivedData).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        thread.start();
    }

    // Método para habilitar/desabilitar botões de saída
    private void enableControlButtons(boolean enable) {
        Button[] buttons = {
                buttonOut01, buttonOut02, buttonOut03, buttonOut04, buttonOut05, buttonOut06,
                buttonOut07, buttonOut08, buttonOut09, buttonOut10, buttonOut11, buttonOut12,
                buttonOut13, buttonOut14, buttonOut15, buttonOut16, buttonOut17, buttonOut18,
                buttonOut19, buttonOut20, buttonOut21, buttonOut22, buttonOut23, buttonOut24
        };

        for (Button button : buttons) {
            button.setEnabled(enable);
        }
        updateButtonColors();
    }

    // Método para atualizar cores dos botões de saída
    private void updateButtonColors() {
        updateButtonOut01Color();
        updateButtonOut02Color();
        updateButtonOut03Color();
        updateButtonOut04Color();
        updateButtonOut05Color();
        updateButtonOut06Color();
        updateButtonOut07Color();
        updateButtonOut08Color();
        updateButtonOut09Color();
        updateButtonOut10Color();
        updateButtonOut11Color();
        updateButtonOut12Color();
        updateButtonOut13Color();
        updateButtonOut14Color();
        updateButtonOut15Color();
        updateButtonOut16Color();
        updateButtonOut17Color();
        updateButtonOut18Color();
        updateButtonOut19Color();
        updateButtonOut20Color();
        updateButtonOut21Color();
        updateButtonOut22Color();
        updateButtonOut23Color();
        updateButtonOut24Color();
    }

    // Método chamado quando permissão é solicitada
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth();
            } else {
                showToast("Bluetooth permission is required to use the app.");
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // onDestroy: método chamado quando a atividade é destruída
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopButtonUpdates();
        disconnectBluetooth();
    }

    // Método para iniciar atualizações periódicas dos botões
    private void startButtonUpdates() {
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    // Método para parar atualizações periódicas dos botões
    private void stopButtonUpdates() {
        updateHandler.removeCallbacks(updateRunnable);
    }
}