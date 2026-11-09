package com.akira.island.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Reply
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FormatLineSpacing
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.UnfoldLess
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.island.core.Lang
import com.akira.island.core.Tints
import com.akira.island.core.tr
import com.akira.island.data.AccentMode
import com.akira.island.data.AppLanguage
import com.akira.island.data.IslandSettings
import com.akira.island.data.SettingsRepo
import com.akira.island.island.FitText
import com.akira.island.island.onAccent
import com.akira.island.island.resolveAccent
import com.akira.island.ui.ChoiceDialog
import com.akira.island.ui.Group
import com.akira.island.ui.GroupItem
import com.akira.island.ui.GroupSlider
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.Page
import com.akira.island.ui.Pos
import com.akira.island.ui.Screen
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile
import com.akira.island.ui.Ui
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.Screenshot
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.ui.graphics.vector.ImageVector
import com.akira.island.data.QuickAction

/**
 * «Функции»: что остров показывает и как с ним обращаться. Внешний вид — во вкладке «Оформление»,
 * версия, помощь и ссылки — в «О приложении».
 */
@Composable
fun FeaturesScreen(open: (Screen) -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var confirmReset by remember { mutableStateOf(false) }
    var languageDialog by remember { mutableStateOf(false) }

    Page(tr("Функции", "Recursos")) {
        SectionHeader(tr("Что показывает остров", "O que a ilha mostra"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.Dashboard, tr("Динамические карточки", "Cartões dinâmicos"),
                tr("Музыка, звонки, таймеры, фонарик, навигация", "Música, chamadas, timers, lanterna, navegação"),
                color = Tile.Purple, onClick = { open(Screen.CARDS) }) { Arrow() }
            GroupItem(Pos.Middle, Icons.Rounded.ChatBubble, tr("Уведомления", "Notificações"),
                tr("Сообщения приложений с ответом прямо в острове", "Mensagens de apps com resposta direto na ilha"),
                color = Tile.Blue, onClick = { open(Screen.NOTIFICATIONS) }) { Arrow() }
            GroupItem(Pos.Middle, Icons.Rounded.NotificationsActive, tr("События", "Eventos"),
                tr("Зарядка, Wi-Fi, наушники, оплата, будильник", "Carregamento, Wi-Fi, fones de ouvido, pagamentos, alarme"),
                color = Tile.Orange, onClick = { open(Screen.EVENTS) }) { Arrow() }
            GroupItem(Pos.Last, Icons.Rounded.LibraryMusic, tr("Текст песен", "Letra"),
                tr("Синхронный текст в раскрытом плеере", "Letra sincronizada no player expandido"),
                color = Tile.Pink, onClick = { open(Screen.LYRICS) }) { Arrow() }
        }

        SectionHeader(tr("Управление", "Controle"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.Gesture, tr("Жесты", "Gestos"), tr("Нажатия и свайпы по капсуле", "Toques e deslizes na cápsula"),
                color = Tile.Teal, onClick = { open(Screen.GESTURES) }) { Arrow() }
            GroupItem(Pos.Middle, Icons.Rounded.Tune, tr("Поведение", "Comportamento"),
                tr("Когда показывать и сворачивать остров, виброотклик", "Quando mostrar e recolher a ilha, vibração"),
                color = Tile.Indigo, onClick = { open(Screen.BEHAVIOR) }) { Arrow() }
            GroupItem(Pos.Last, Icons.Rounded.NotificationsOff, tr("Системные плашки", "Banners do sistema"),
                tr("Чтобы звонок, таймер и батарею показывал только остров", "Para só a ilha mostrar chamadas, timers e bateria"),
                color = Tile.Red, onClick = { open(Screen.BANNERS) }) { Arrow() }
        }

        SectionHeader(tr("Общие", "Geral"))
        Group {
            // Значок глобуса и «Language» рядом — язык найдётся, даже если сейчас он непонятен
            GroupItem(Pos.First, Icons.Rounded.Language, if (Lang.pt) "Idioma" else "Язык · Language", s.language.title,
                color = Tile.Blue, onClick = { languageDialog = true }) { Arrow() }
            GroupItem(Pos.Last, Icons.Rounded.Restore, tr("Сбросить все настройки", "Redefinir todas as configurações"),
                tr("Вернуть всё как было после установки", "Voltar ao estado de logo após a instalação"),
                onClick = { confirmReset = true }, color = Tile.Gray)
        }
    }

    if (languageDialog) {
        ChoiceDialog(if (Lang.pt) "Idioma" else "Язык · Language", AppLanguage.entries, s.language, { it.title },
            { v -> SettingsRepo.update { copy(language = v) } }) { languageDialog = false }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = Ui.Card,
            title = { Text(tr("Сбросить настройки?", "Redefinir configurações?")) },
            text = {
                Text(tr("Размер, цвета, анимации, карточки и события вернутся к значениям по умолчанию.",
                    "Tamanho, cores, animações, cartões e eventos voltam aos valores padrão."))
            },
            confirmButton = {
                TextButton(onClick = {
                    SettingsRepo.reset()
                    confirmReset = false
                }) { Text(tr("Сбросить", "Redefinir"), color = Ui.Primary) }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(tr("Отмена", "Cancelar"), color = Ui.Sub) } },
        )
    }
}

/** «Поведение»: когда остров виден, как сворачивается и чем отзывается. */
@Composable
fun BehaviorScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var collapseDialog by remember { mutableStateOf(false) }

    Page(tr("Поведение", "Comportamento"), onBack) {
        SectionHeader(tr("Когда показывать", "Quando mostrar"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.VisibilityOff, tr("Скрывать пустую капсулу", "Ocultar a cápsula vazia"),
                tr("Показывать капсулу, только когда в ней что-то есть", "Mostrar a cápsula só quando houver algo nela"),
                s.hideIdle, color = Tile.Gray) { v -> SettingsRepo.update { copy(hideIdle = v) } }
            GroupToggle(Pos.Middle, Icons.Rounded.ScreenRotation, tr("Скрывать в альбомной ориентации", "Ocultar no modo paisagem"),
                tr("Не мешает играм и видео", "Não atrapalha jogos e vídeos"),
                s.hideInLandscape, color = Tile.Gray) { v -> SettingsRepo.update { copy(hideInLandscape = v) } }
            GroupToggle(Pos.Last, Icons.Rounded.Apps, tr("Прятать плеер в его приложении", "Ocultar o player no próprio app"),
                tr("Карточка музыки не показывается, пока открыт сам плеер", "O cartão de música fica oculto enquanto o próprio player está aberto"),
                s.hideWhenPlayerOpen, color = Tile.Blue) { v -> SettingsRepo.update { copy(hideWhenPlayerOpen = v) } }
        }

        // Например, только днём: с 10:00 до 23:00; можно и через полночь — с 22:00 до 7:00
        SectionHeader(tr("Расписание", "Agenda"))
        Group {
            GroupToggle(if (s.scheduleOn) Pos.First else Pos.Single, Icons.Rounded.Schedule, tr("По расписанию", "Por horário"),
                tr("Остров виден только с ${hourLabel(s.scheduleFrom)} до ${hourLabel(s.scheduleTo)}",
                    "A ilha aparece só das ${hourLabel(s.scheduleFrom)} às ${hourLabel(s.scheduleTo)}"),
                s.scheduleOn, color = Tile.Indigo) { v -> SettingsRepo.update { copy(scheduleOn = v) } }
            if (s.scheduleOn) {
                GroupSlider(Pos.Middle, Icons.Rounded.WbSunny, tr("С", "Das"), s.scheduleFrom.toFloat(), 0f..23f,
                    hourLabel(s.scheduleFrom), steps = 22, color = Tile.Orange) { v -> SettingsRepo.update { copy(scheduleFrom = v.toInt()) } }
                GroupSlider(Pos.Last, Icons.Rounded.Bedtime, tr("До", "Às"), s.scheduleTo.toFloat(), 0f..23f,
                    hourLabel(s.scheduleTo), steps = 22, color = Tile.Purple) { v -> SettingsRepo.update { copy(scheduleTo = v.toInt()) } }
            }
        }

        SectionHeader(tr("Раскрытый остров", "Ilha expandida"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.UnfoldLess, tr("Сворачивать раскрытый остров", "Recolher a ilha expandida"), collapseLabel(s.autoCollapseSec),
                color = Tile.Indigo, onClick = { collapseDialog = true })
            GroupToggle(Pos.Last, Icons.Rounded.Circle, tr("Вторая активность рядом", "Segunda atividade ao lado"),
                tr("Когда активностей две, вторая — кружком рядом с капсулой, как на iPhone",
                    "Com duas atividades, a segunda aparece como um círculo pequeno ao lado da cápsula, como no iPhone"),
                s.bubble, color = Tile.Purple) { v ->
                SettingsRepo.update { copy(bubble = v) }
            }
        }

        // Нажатие на пустой остров: лишние кнопки убираются, а можно не раскрывать его вовсе
        SectionHeader(tr("Пустой остров по нажатию", "Tocar na ilha vazia"))
        Group {
            GroupToggle(if (s.idleExpand) Pos.First else Pos.Single, Icons.Rounded.UnfoldMore, tr("Раскрывать", "Expandir"),
                tr("Часы, заряд и кнопки ниже. Выключено — нажатие ничего не делает",
                    "Relógio, bateria e os botões abaixo. Desligado — o toque não faz nada"),
                s.idleExpand, color = Tile.Blue) { v -> SettingsRepo.update { copy(idleExpand = v) } }
            if (s.idleExpand) {
                QuickAction.entries.forEachIndexed { i, a ->
                    GroupToggle(if (i == QuickAction.entries.lastIndex) Pos.Last else Pos.Middle, a.settingsIcon, a.title,
                        a.hint, a in s.quickActions, color = Tile.Orange) { v ->
                        // Порядок кнопок — как в списке, что бы ни включали первым
                        SettingsRepo.update { copy(quickActions = QuickAction.entries.filter { if (it == a) v else it in quickActions }) }
                    }
                }
            }
        }

        SectionHeader(tr("Отклик", "Resposta ao toque"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.Vibration, tr("Виброотклик", "Vibração ao toque"),
                tr("При открытии, закрытии и нажатиях на капсулу", "Ao abrir, fechar e tocar na cápsula"),
                s.haptics, color = Tile.Pink) { v -> SettingsRepo.update { copy(haptics = v) } }
            GroupToggle(Pos.Last, Icons.Rounded.LightMode, tr("Включать экран", "Ligar a tela"),
                tr("Когда звонит таймер, будильник или входящий вызов — экран включается и не гаснет, пока звонит",
                    "Quando um timer, alarme ou chamada recebida toca, a tela liga e fica ligada enquanto toca"),
                s.wakeForAlerts, color = Tile.Yellow) { v -> SettingsRepo.update { copy(wakeForAlerts = v) } }
        }
    }

    if (collapseDialog) {
        ChoiceDialog(tr("Сворачивать раскрытый остров", "Recolher a ilha expandida"), listOf(0, 5, 10, 20, 30), s.autoCollapseSec, ::collapseLabel,
            { v -> SettingsRepo.update { copy(autoCollapseSec = v) } }) { collapseDialog = false }
    }
}

/** Час расписания: «10:00». */
private fun hourLabel(h: Int) = "%d:00".format(h)

/** Подпись для «Сворачивать раскрытый остров». */
private fun collapseLabel(sec: Int) = if (sec <= 0) tr("Только касанием мимо или свайпом вверх", "Só tocando fora ou deslizando para cima")
    else tr("Через $sec секунд без касаний", "Após $sec segundos sem toque")

private val accentColors = listOf(
    0xFF0A84FF, 0xFFC6C3F7, 0xFF64D2FF, 0xFF34C759, 0xFFFFD60A, 0xFFFF9F0A,
    0xFFFF453A, 0xFFFF2D92, 0xFFBF5AF2, 0xFFFFFFFF,
)

/** «Уведомления»: сообщения приложений в острове — что показывать, сколько текста и каким цветом кнопки. */
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var accentDialog by remember { mutableStateOf(false) }

    Page(tr("Уведомления", "Notificações"), onBack) {
        Group {
            GroupToggle(Pos.First, Icons.Rounded.ChatBubble, tr("Уведомления приложений", "Notificações de apps"),
                tr("Сообщения из Telegram, WhatsApp, почты и других приложений — карточкой в острове, как на iPhone",
                    "Mensagens do Telegram, WhatsApp, e-mail e outros apps como um cartão na ilha, como no iPhone"),
                s.cardMessages, color = Tile.Blue) { v -> SettingsRepo.update { copy(cardMessages = v) } }
            GroupToggle(Pos.Middle, Icons.AutoMirrored.Rounded.Reply, tr("Кнопки в уведомлениях", "Botões nas notificações"),
                tr("«Ответить» с вводом текста прямо в острове и «Пометить прочитанным»", "Responder digitando direto na ilha e Marcar como lida"),
                s.msgActions, color = Tile.Indigo) { v -> SettingsRepo.update { copy(msgActions = v) } }
            GroupToggle(Pos.Middle, Icons.Rounded.Smartphone, tr("Из открытого приложения", "Do app aberto"),
                tr("Показывать и уведомления приложения, которое сейчас на экране", "Mostrar também as notificações do app que está na tela"),
                s.msgInApp, color = Tile.Teal) { v ->
                SettingsRepo.update { copy(msgInApp = v) }
            }
            GroupSlider(
                Pos.Last, Icons.Rounded.FormatLineSpacing, tr("Строк текста", "Linhas de texto"),
                value = s.msgLines.toFloat(), range = 1f..6f, steps = 4, label = "${s.msgLines}", color = Tile.Green,
            ) { v -> SettingsRepo.update { copy(msgLines = Math.round(v)) } }
        }

        SectionHeader(tr("Цвет кнопок", "Cor dos botões"))
        AccentPreview(s)
        GroupItem(Pos.Single, Icons.Rounded.Palette, tr("Цвет кнопок", "Cor dos botões"), "${s.accentMode.title} — ${s.accentMode.hint}",
            onClick = { accentDialog = true }, color = Tile.Orange)
        if (s.accentMode == AccentMode.CUSTOM) {
            Swatches(accentColors, s.accentColor) { c -> SettingsRepo.update { copy(accentColor = c) } }
        }
    }

    if (accentDialog) {
        ChoiceDialog(tr("Цвет кнопок", "Cor dos botões"), AccentMode.entries, s.accentMode, { "${it.title} — ${it.hint}" },
            { v -> SettingsRepo.update { copy(accentMode = v) } }) { accentDialog = false }
    }
}

/** Как выглядят «Ответить», «Пометить прочитанным» и кнопка отправки с выбранным цветом. */
@Composable
private fun AccentPreview(s: IslandSettings) {
    val context = LocalContext.current
    // В режиме «Из аватара» цвет зависит от самого уведомления — в примере берём оранжевый
    val accent = resolveAccent(context, s, Tints.Orange)
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color.Black).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PreviewTile(tr("Ответить", "Responder"), accent, Modifier.weight(1f))
        PreviewTile(tr("Пометить прочитанным", "Marcar como lida"), accent, Modifier.weight(2f))
        Box(Modifier.size(40.dp).clip(CircleShape).background(accent), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.ArrowUpward, null, tint = onAccent(accent), modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun PreviewTile(title: String, accent: Color, modifier: Modifier) {
    Box(
        modifier.height(40.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.12f)).padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        FitText(title, accent, 14.sp, 10.sp, FontWeight.SemiBold)
    }
}

private val QuickAction.settingsIcon: ImageVector
    get() = when (this) {
        QuickAction.TIMER -> Icons.Rounded.HourglassBottom
        QuickAction.STOPWATCH -> Icons.Rounded.Timer
        QuickAction.FLASHLIGHT -> Icons.Rounded.FlashlightOn
        QuickAction.SCREENSHOT -> Icons.Rounded.Screenshot
        QuickAction.CAMERA -> Icons.Rounded.PhotoCamera
        QuickAction.LOCK -> Icons.Rounded.Lock
    }

/** Подпись к кнопке — только там, где непонятно по названию */
private val QuickAction.hint: String?
    get() = when (this) {
        QuickAction.SCREENSHOT -> tr("Остров на миг прячется — на снимке его нет (Android 9+)",
            "A ilha se esconde por um instante, então não aparece na captura (Android 9+)")
        QuickAction.LOCK -> tr("Гасит экран, как кнопка питания (Android 9+)", "Apaga a tela como o botão de energia (Android 9+)")
        else -> null
    }
