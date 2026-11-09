package com.akira.island.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.KeyboardVoice
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.akira.island.core.tr
import com.akira.island.data.IslandSettings
import com.akira.island.data.SettingsRepo
import com.akira.island.ui.Group
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.IconAction
import com.akira.island.ui.Page
import com.akira.island.ui.Pos
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile

/** «Динамические карточки»: что остров показывает, пока это идёт, — по разделам. */
@Composable
fun CardsScreen(onBack: () -> Unit, onInstruction: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    fun set(block: IslandSettings.() -> IslandSettings) = SettingsRepo.update(block)

    Page(tr("Динамические карточки", "Cartões dinâmicos"), onBack,
        action = { IconAction(Icons.Rounded.Lightbulb, tr("Инструкция", "Instruções"), onInstruction) }) {
        SectionHeader(tr("Музыка и звонки", "Música e chamadas"))
        Group {
            GroupToggle(Pos.First, Icons.Outlined.PlayCircle, tr("Управление проигрывателем", "Controles do player"),
                tr("Когда играет музыка и плеер свёрнут", "Quando a música está tocando e o player está em segundo plano"),
                s.cardMedia, color = Tile.Pink) { set { copy(cardMedia = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.Call, tr("Звонки", "Chamadas"),
                tr("Входящий вызов с кнопками ответа и время разговора", "Chamadas recebidas com botões de atender e o tempo de chamada"),
                s.cardCall, color = Tile.Green) { set { copy(cardCall = it) } }
        }

        SectionHeader(tr("Таймеры и будильник", "Timers e alarme"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.HourglassEmpty, tr("Таймер и секундомер", "Timer e cronômetro"),
                tr("Таймер и секундомер «Часов» в фоне — даже если их уведомления выключены",
                    "Timer e cronômetro do Relógio em segundo plano — mesmo com as notificações dele desligadas"),
                s.cardTimer, color = Tile.Orange) { set { copy(cardTimer = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.TouchApp, tr("Кнопки для «Часов»", "Botões do Relógio"),
                tr("Пауза и сброс в острове нажимают кнопки самих «Часов». Выключено — просто открывают «Часы»",
                    "Pausar e zerar na ilha apertam os botões do próprio Relógio. Desligado — apenas abrem o Relógio"),
                s.clockAutoTap, color = Tile.Orange) { set { copy(clockAutoTap = it) } }
            GroupToggle(Pos.Middle, Icons.Rounded.KeyboardVoice, tr("Таймер от помощника", "Timer do assistente"),
                tr("«Поставь таймер на 5 минут» от помощника или виджета можно отдать острову",
                    "“Defina um timer de 5 minutos”, dito a um assistente ou widget, pode ser entregue à ilha"),
                s.timerFromAssistant, color = Tile.Pink) { set { copy(timerFromAssistant = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.Bedtime, tr("Будильник", "Alarme"),
                tr("За час до будильника — время подъёма и циферблат сна, как на iPhone",
                    "1 hora antes de um alarme — o horário de acordar e um mostrador de sono, como no iPhone"),
                s.cardAlarm, color = Tile.Indigo) { set { copy(cardAlarm = it) } }
        }

        SectionHeader(tr("Система", "Sistema"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.FlashlightOn, tr("Управление вспышкой", "Controles da lanterna"),
                tr("Когда включён фонарик", "Quando a lanterna está ligada"),
                s.cardTorch, color = Tile.Yellow) { set { copy(cardTorch = it) } }
            GroupToggle(Pos.Last, Icons.Rounded.Downloading, tr("Индикатор прогресса", "Indicador de progresso"),
                tr("Загрузки и отправка в Google Play, Telegram и других приложениях", "Downloads e uploads na Google Play, no Telegram e em outros apps"),
                s.cardProgress, color = Tile.Teal) { set { copy(cardProgress = it) } }
        }

        SectionHeader(tr("В дороге", "No trajeto"))
        GroupToggle(Pos.Single, Icons.Rounded.Navigation, tr("Навигация, такси и доставка", "Navegação, táxi e entrega"),
            tr("Google и Яндекс Карты, Навигатор, 2ГИС, Яндекс Go, Uber и другие — расстояние и время в капсуле, подробности в раскрытом виде",
                "Google e Yandex Maps, Navigator, 2GIS, Yandex Go, Uber e outros — distância e tempo na cápsula, detalhes quando expandida"),
            s.cardLive, color = Tile.Teal) { set { copy(cardLive = it) } }

        SectionHeader(tr("Сюрпризы", "Surpresas"))
        GroupToggle(Pos.Single, Icons.Rounded.AutoAwesome, tr("Пасхалки", "Ovos de Páscoa"),
            tr("Одну особенную песню остров узнаёт и устраивает для неё маленькое шоу в такт музыке. Какую — попробуйте угадать",
                "A ilha reconhece uma música especial e faz um pequeno show no ritmo dela. Qual? Tente adivinhar"),
            s.easterEggs, color = Tile.Red) { set { copy(easterEggs = it) } }
    }
}
