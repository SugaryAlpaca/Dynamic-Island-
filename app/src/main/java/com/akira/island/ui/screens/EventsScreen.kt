package com.akira.island.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.AirplanemodeInactive
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.BluetoothDisabled
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.PowerOff
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.UsbOff
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.akira.island.core.tr
import com.akira.island.data.IslandSettings
import com.akira.island.data.SettingsRepo
import com.akira.island.ui.Group
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.Page
import com.akira.island.ui.Pos
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile

/** «События»: короткие плашки о том, что произошло, — по разделам. */
@Composable
fun EventsScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    fun set(block: IslandSettings.() -> IslandSettings) = SettingsRepo.update(block)

    Page(tr("События", "Eventos"), onBack) {
        SectionHeader(tr("Батарея", "Bateria"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.Bolt, tr("Зарядка началась", "Carregamento iniciado"), null,
                s.evChargeStart, color = Tile.Green) { set { copy(evChargeStart = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.PowerOff, tr("Зарядка остановлена", "Carregamento interrompido"), null,
                s.evChargeStop, color = Tile.Gray) { set { copy(evChargeStop = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.BatteryAlert, tr("Низкий заряд", "Bateria fraca"),
                tr("Когда заряд батареи падает до 5%", "Quando a bateria cai para 5%"),
                s.evLowBattery, color = Tile.Red) { set { copy(evLowBattery = it) } }
        }

        SectionHeader(tr("Подключения", "Conexões"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.Wifi, tr("Wi-Fi подключён", "Wi-Fi conectado"), null,
                s.evWifiOn, color = Tile.Blue) { set { copy(evWifiOn = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.WifiOff, tr("Wi-Fi отключён", "Wi-Fi desconectado"), null,
                s.evWifiOff, color = Tile.Gray) { set { copy(evWifiOff = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.BluetoothConnected, tr("Наушники подключены", "Fones de ouvido conectados"),
                tr("По Bluetooth, с зарядом, если он известен", "Via Bluetooth, com a carga da bateria, se disponível"),
                s.evHeadsetOn, color = Tile.Indigo) { set { copy(evHeadsetOn = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.BluetoothDisabled, tr("Наушники отключены", "Fones de ouvido desconectados"), null,
                s.evHeadsetOff, color = Tile.Gray) { set { copy(evHeadsetOff = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.Usb, tr("USB-устройство подключено", "Dispositivo USB conectado"), null,
                s.evUsbOn, color = Tile.Teal) { set { copy(evUsbOn = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.UsbOff, tr("USB-устройство отключено", "Dispositivo USB desconectado"), null,
                s.evUsbOff, color = Tile.Gray) { set { copy(evUsbOff = it) } }
        }

        SectionHeader(tr("Режимы", "Modos"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.AirplanemodeActive, tr("Режим полёта включён", "Modo avião ligado"), null,
                s.evAirplaneOn, color = Tile.Orange) { set { copy(evAirplaneOn = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.AirplanemodeInactive, tr("Режим полёта выключен", "Modo avião desligado"), null,
                s.evAirplaneOff, color = Tile.Gray) { set { copy(evAirplaneOff = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.DoNotDisturbOn, tr("Не беспокоить", "Não perturbe"),
                tr("Луна и состояние при переключении", "Uma lua e o estado quando ele muda"),
                s.evDnd, color = Tile.Indigo) { set { copy(evDnd = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.Vibration, tr("Режим громкости", "Modo de toque"),
                tr("Звонок, вибрация или без звука", "Som, vibrar ou silencioso"),
                s.evRinger, color = Tile.Pink) { set { copy(evRinger = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.BrightnessAuto, tr("Режим яркости", "Modo de brilho"),
                tr("Когда включают или выключают адаптивную яркость", "Quando o brilho adaptativo é ligado ou desligado"),
                s.evBrightness, color = Tile.Yellow) { set { copy(evBrightness = it) } }
        }

        SectionHeader(tr("Оплата", "Pagamentos"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.Nfc, tr("Приложите к терминалу", "Aproxime do leitor"),
                tr("Когда на экране оплата телефоном — Mir Pay, T-Pay, SberPay, Яндекс Пэй и другие. Держится, пока не заплатите или не выйдете из приложения",
                    "Quando há um pagamento por celular na tela — Mir Pay, T-Pay, SberPay, Yandex Pay e outros. Fica até você pagar ou sair do app"),
                s.evPayReady, color = Tile.Blue) { set { copy(evPayReady = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.AccountBalance, tr("Подсказка при открытии банка", "Dica ao abrir o banco"),
                tr("«К терминалу» сразу, как открыли банк или кошелёк, а не только на экране оплаты",
                    "“Aproxime do leitor” assim que você abre um app de banco ou carteira, não só na tela de pagamento"),
                s.payHintOnOpen, color = Tile.Gray) { set { copy(payHintOnOpen = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.CheckCircle, tr("Оплачено", "Pago"),
                tr("Галочка и сумма — с экрана кошелька, из уведомления или СМС банка",
                    "Um check e o valor — da tela da carteira, de uma notificação ou de um SMS do banco"),
                s.evPaySuccess, color = Tile.Green) { set { copy(evPaySuccess = it) } }
        }

        SectionHeader(tr("Другое", "Outros"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.Alarm, tr("Будильник поставлен", "Alarme definido"),
                tr("Время подъёма и сколько осталось спать", "Horário de acordar e quanto tempo você ainda pode dormir"),
                s.evAlarmSet, color = Tile.Orange) { set { copy(evAlarmSet = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.Lock, tr("Замок на экране блокировки", "Cadeado na tela de bloqueio"),
                tr("Пока телефон заблокирован, в капсуле замок", "Enquanto o celular está bloqueado, a cápsula mostra um cadeado"),
                s.evLock, color = Tile.Indigo) { set { copy(evLock = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.LockOpen, tr("Разблокировка устройства", "Desbloqueio do aparelho"),
                tr("Замок открывается, когда вы разблокируете телефон", "O cadeado se abre quando você desbloqueia o celular"),
                s.evUnlock, color = Tile.Purple) { set { copy(evUnlock = it) } }
        }
    }
}
