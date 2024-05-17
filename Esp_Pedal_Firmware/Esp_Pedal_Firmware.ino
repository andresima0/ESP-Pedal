#include <BluetoothSerial.h>

BluetoothSerial SerialBT;


const int OUT_1 = 2;    // Pino do OUT 1 (GPIO 2)
const int OUT_2 = 4;    // Pino do OUT 2 (GPIO 4)
const int OUT_3 = 5;    // Pino do OUT 3 (GPIO 5)
const int OUT_4 = 18;   // Pino do OUT 4 (GPIO 18)
const int OUT_5 = 19;   // Pino do OUT 5 (GPIO 19)
const int OUT_6 = 21;   // Pino do OUT 6 (GPIO 21)
const int OUT_7 = 22;   // Pino do OUT 7 (GPIO 22)
const int OUT_8 = 23;   // Pino do OUT 8 (GPIO 23)

const int SWITCH_PIN = 13;  // Pino do switch (GPIO 13)

const int PRESET_1 = 25;    // Pino do PRESET 1
const int PRESET_2 = 26;    // Pino do PRESET 2
const int PRESET_3 = 27;    // Pino do PRESET 3

int currentPreset = 0;
int buttonState = 0;
int lastButtonState = HIGH;
unsigned long lastDebounceTime = 0;
unsigned long debounceDelay = 50;

bool presetState[] = { false, false, false };

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP Pedal");  // Nome do dispositivo Bluetooth

  // Configuração dos pinos dos OUTs
  pinMode(OUT_1, OUTPUT);
  pinMode(OUT_2, OUTPUT);
  pinMode(OUT_3, OUTPUT);
  pinMode(OUT_4, OUTPUT);
  pinMode(OUT_5, OUTPUT);
  pinMode(OUT_6, OUTPUT);
  pinMode(OUT_7, OUTPUT);
  pinMode(OUT_8, OUTPUT);

  // Configuração do pino do switch
  pinMode(SWITCH_PIN, INPUT_PULLUP);

  // Configuração dos pinos dos presets
  pinMode(PRESET_1, OUTPUT);
  pinMode(PRESET_2, OUTPUT);
  pinMode(PRESET_3, OUTPUT);

  // Inicialmente, os OUTs e presets estão desligados
  digitalWrite(OUT_1, LOW);
  digitalWrite(OUT_2, LOW);
  digitalWrite(OUT_3, LOW);
  digitalWrite(OUT_4, LOW);
  digitalWrite(OUT_5, LOW);
  digitalWrite(OUT_6, LOW);
  digitalWrite(OUT_7, LOW);
  digitalWrite(OUT_8, LOW);
  digitalWrite(PRESET_1, LOW);
  digitalWrite(PRESET_2, LOW);
  digitalWrite(PRESET_3, LOW);
}

void loop() {
  // Controle dos presets
  int reading = digitalRead(SWITCH_PIN);

  if (reading != lastButtonState) {
    lastDebounceTime = millis();
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {
    if (reading != buttonState) {
      buttonState = reading;

      if (buttonState == LOW) {
        // Alterna o estado do preset atual e desliga o preset anterior
        presetState[currentPreset] = !presetState[currentPreset];
        digitalWrite(PRESET_1, presetState[0]);
        digitalWrite(PRESET_2, presetState[1]);
        digitalWrite(PRESET_3, presetState[2]);

        // Desliga o preset anterior
        int previousPreset = (currentPreset + 2) % 3;
        presetState[previousPreset] = false;
        digitalWrite(PRESET_1, presetState[0]);
        digitalWrite(PRESET_2, presetState[1]);
        digitalWrite(PRESET_3, presetState[2]);

        // Envia o sinal Bluetooth com base no preset atual
        char presetChar = '0' + (currentPreset + 1);  // Convertendo o número do preset para char
        SerialBT.write(presetChar);

        // Atualiza o preset atual para o próximo
        currentPreset = (currentPreset + 1) % 3;
      }
    }
  }

  lastButtonState = reading;

  // Controle dos OUTs via Bluetooth
  if (SerialBT.available()) {
    char receivedChar = SerialBT.read();
    // Verifica qual botão foi pressionado e liga ou desliga o OUT correspondente
    if (currentPreset == 1) {
      switch (receivedChar) {
        case 'A':
          digitalWrite(OUT_1, HIGH);
          break;
        case 'a':
          digitalWrite(OUT_1, LOW);
          break;
        case 'B':
          digitalWrite(OUT_2, HIGH);
          break;
        case 'b':
          digitalWrite(OUT_2, LOW);
          break;
        case 'C':
          digitalWrite(OUT_3, HIGH);
          break;
        case 'c':
          digitalWrite(OUT_3, LOW);
          break;
        case 'D':
          digitalWrite(OUT_4, HIGH);
          break;
        case 'd':
          digitalWrite(OUT_4, LOW);
          break;
        case 'E':
          digitalWrite(OUT_5, HIGH);
          break;
        case 'e':
          digitalWrite(OUT_5, LOW);
          break;
        case 'F':
          digitalWrite(OUT_6, HIGH);
          break;
        case 'f':
          digitalWrite(OUT_6, LOW);
          break;
        case 'G':
          digitalWrite(OUT_7, HIGH);
          break;
        case 'g':
          digitalWrite(OUT_7, LOW);
          break;
        case 'H':
          digitalWrite(OUT_8, HIGH);
          break;
        case 'h':
          digitalWrite(OUT_8, LOW);
          break;
      }
    } else if (currentPreset == 2) {
      switch (receivedChar) {
        case 'I':
          digitalWrite(OUT_1, HIGH);
          break;
        case 'i':
          digitalWrite(OUT_1, LOW);
          break;
        case 'J':
          digitalWrite(OUT_2, HIGH);
          break;
        case 'j':
          digitalWrite(OUT_2, LOW);
          break;
        case 'K':
          digitalWrite(OUT_3, HIGH);
          break;
        case 'k':
          digitalWrite(OUT_3, LOW);
          break;
        case 'L':
          digitalWrite(OUT_4, HIGH);
          break;
        case 'l':
          digitalWrite(OUT_4, LOW);
          break;
        case 'M':
          digitalWrite(OUT_5, HIGH);
          break;
        case 'm':
          digitalWrite(OUT_5, LOW);
          break;
        case 'N':
          digitalWrite(OUT_6, HIGH);
          break;
        case 'n':
          digitalWrite(OUT_6, LOW);
          break;
        case 'O':
          digitalWrite(OUT_7, HIGH);
          break;
        case 'o':
          digitalWrite(OUT_7, LOW);
          break;
        case 'P':
          digitalWrite(OUT_8, HIGH);
          break;
        case 'p':
          digitalWrite(OUT_8, LOW);
          break;
      }
    } else if (currentPreset == 0) {
      switch (receivedChar) {
        case 'Q':
          digitalWrite(OUT_1, HIGH);
          break;
        case 'q':
          digitalWrite(OUT_1, LOW);
          break;
        case 'R':
          digitalWrite(OUT_2, HIGH);
          break;
        case 'r':
          digitalWrite(OUT_2, LOW);
          break;
        case 'S':
          digitalWrite(OUT_3, HIGH);
          break;
        case 's':
          digitalWrite(OUT_3, LOW);
          break;
        case 'T':
          digitalWrite(OUT_4, HIGH);
          break;
        case 't':
          digitalWrite(OUT_4, LOW);
          break;
        case 'U':
          digitalWrite(OUT_5, HIGH);
          break;
        case 'u':
          digitalWrite(OUT_5, LOW);
          break;
        case 'V':
          digitalWrite(OUT_6, HIGH);
          break;
        case 'v':
          digitalWrite(OUT_6, LOW);
          break;
        case 'W':
          digitalWrite(OUT_7, HIGH);
          break;
        case 'w':
          digitalWrite(OUT_7, LOW);
          break;
        case 'X':
          digitalWrite(OUT_8, HIGH);
          break;
        case 'x':
          digitalWrite(OUT_8, LOW);
          break;
      }
    }
  }
}