package com.akira.island.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.BubbleChart
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.Flare
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LineWeight
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.island.core.IslandBus
import com.akira.island.core.Tints
import com.akira.island.core.tr
import com.akira.island.data.Elasticity
import com.akira.island.data.ExpandStyle
import com.akira.island.data.FxRate
import com.akira.island.data.IslandSettings
import com.akira.island.data.SettingsRepo
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

/**
 * «Оформление»: где стоит остров и как он выглядит — положение и размер, цвет и стекло, анимации и эффекты.
 * Что остров показывает и как себя ведёт — во вкладке «Функции».
 */
@Composable
fun CapsuleScreen(open: (Screen) -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    Page(tr("Оформление", "Aparência")) {
        IslandPreview()

        SectionHeader(tr("Положение и размер", "Posição e tamanho"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.OpenWith, tr("Положение и размер", "Posição e tamanho"),
                tr("Капсула ${s.width}×${s.height} dp — подогнать под камеру", "Cápsula ${s.width}×${s.height} dp — ajuste à câmera"),
                color = Tile.Indigo, onClick = { open(Screen.SIZE) }) { Arrow() }
            GroupItem(Pos.Last, Icons.Rounded.SwapHoriz, tr("Размер с активностью", "Tamanho com atividade"),
                if (s.activeWidth == 0) tr("Толщина +${s.activeExtra} dp, ширина как без неё", "Espessura +${s.activeExtra} dp, largura igual à sem atividade")
                else tr("Толщина +${s.activeExtra}, ширина +${s.activeWidth} dp", "Espessura +${s.activeExtra}, largura +${s.activeWidth} dp"),
                color = Tile.Purple, onClick = { open(Screen.LOOK) }) { Arrow() }
        }

        SectionHeader(tr("Цвет и стекло", "Cor e vidro"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.FormatColorFill, tr("Цвет капсулы", "Cor da cápsula"), tr("Цвет, контур и тень", "Cor, contorno e sombra"),
                color = Tile.Orange, onClick = { open(Screen.COLORS) }) { Arrow() }
            GroupToggle(
                if (s.liquidGlass) Pos.Middle else Pos.Last, Icons.Rounded.WaterDrop, "Liquid Glass",
                tr("Стекло с бликами и сияющей кромкой, как в iOS. Блики двигаются при наклоне",
                    "Vidro com reflexos e borda brilhante, como no iOS. Os reflexos se movem quando você inclina o celular"),
                s.liquidGlass, color = Tile.Teal, badge = tr("Экспериментальный", "Experimental"),
            ) { v -> SettingsRepo.update { copy(liquidGlass = v) } }
            if (s.liquidGlass) {
                GroupSlider(
                    Pos.Last, Icons.Rounded.Opacity, tr("Плотность стекла", "Densidade do vidro"),
                    value = s.glassOpacity.toFloat(), range = 40f..95f, label = "${s.glassOpacity}%", color = Tile.Teal,
                ) { v -> SettingsRepo.update { copy(glassOpacity = v.toInt()) } }
            }
        }

        SectionHeader(tr("Анимации", "Animações"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.Animation, tr("Раскрытие острова", "Expansão da ilha"),
                "${s.expandStyle.title} · ${s.durationPercent}%",
                color = Tile.Blue, onClick = { open(Screen.ANIM) }) { Arrow() }
            // Новое раскрытие — отдельным переключателем: выключено — прежнее перетекание
            GroupToggle(Pos.Middle, Icons.Rounded.BubbleChart, tr("Жидкое раскрытие", "Expansão líquida"),
                tr("Содержимое растёт вместе с островом и проявляется из размытия",
                    "O conteúdo cresce junto com a ilha e surge de um desfoque"),
                s.liquidExpand, color = Tile.Indigo, badge = tr("Новое", "Novo"),
            ) { v -> SettingsRepo.update { copy(liquidExpand = v) } }
            // Упругость — на виду: «слишком прыгает» правится одним нажатием
            ElasticityItem(Pos.Middle, s, Tile.Teal)
            GroupItem(Pos.Last, Icons.Rounded.AutoAwesome, tr("Эффекты", "Efeitos"), effectsSummary(s),
                color = Tile.Purple, onClick = { open(Screen.EFFECTS) }) { Arrow() }
        }
    }
}

/** Пункт «Упругость» с выбором — прямо в «Оформлении», рядом с «Раскрытием острова». */
@Composable
private fun ElasticityItem(pos: Pos, s: IslandSettings, color: Color) {
    var dialog by remember { mutableStateOf(false) }
    GroupItem(pos, Icons.Rounded.Waves, tr("Упругость", "Elasticidade"), s.elasticity.title, color = color, onClick = { dialog = true })
    if (dialog) {
        ChoiceDialog(tr("Упругость анимации", "Elasticidade da animação"), Elasticity.entries, s.elasticity, { it.title },
            { v -> SettingsRepo.update { copy(elasticity = v) } }) { dialog = false }
    }
}

/** Пока двигают ползунок размера, на острове пример активности — сразу видно, какой она будет. */
private fun previewActivity() {
    IslandBus.widthPreviewUntil.value = SystemClock.elapsedRealtime() + 2_500
}

/** «Размер с активностью»: насколько капсула с навигатором, фонариком или музыкой толще и шире спокойной. */
@Composable
fun LookScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    Page(tr("Размер с активностью", "Tamanho com atividade"), onBack) {
        IslandPreview()
        Group {
            GroupSlider(
                Pos.First, Icons.Rounded.LineWeight, tr("Толщина", "Espessura"),
                value = s.activeExtra.toFloat(), range = 0f..14f, label = "+${s.activeExtra} dp", steps = 13, color = Tile.Purple,
            ) { v ->
                previewActivity()
                SettingsRepo.update { copy(activeExtra = v.toInt()) }
            }
            GroupSlider(
                Pos.Last, Icons.Rounded.SwapHoriz, tr("Ширина", "Largura"),
                value = s.activeWidth.toFloat(), range = 0f..120f, steps = 23,
                label = if (s.activeWidth == 0) tr("Как без неё", "Como sem atividade") else "+${s.activeWidth} dp", color = Tile.Blue,
            ) { v ->
                // Сразу видно, не налезает ли капсула на значки строки состояния
                previewActivity()
                SettingsRepo.update { copy(activeWidth = (v / 5).toInt() * 5) }
            }
        }
    }
}

/** «Раскрытие острова»: как остров раскрывается, насколько пружинит и сколько видно события. */
@Composable
fun AnimScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var styleDialog by remember { mutableStateOf(false) }

    Page(tr("Раскрытие острова", "Expansão da ilha"), onBack, action = {
        Text(tr("Сброс", "Redefinir"), color = Ui.Sub, fontSize = 16.sp, modifier = Modifier.clickable { SettingsRepo.resetAnimations() })
    }) {
        Group {
            GroupItem(Pos.First, Icons.Rounded.Animation, tr("Стиль раскрытия", "Estilo de expansão"), s.expandStyle.title, color = Tile.Purple,
                onClick = { styleDialog = true })
            GroupSlider(
                Pos.Middle, Icons.Rounded.Speed, tr("Длительность", "Duração"),
                value = s.durationPercent.toFloat(), range = 0f..100f, label = "${s.durationPercent}%", color = Tile.Indigo,
            ) { v -> SettingsRepo.update { copy(durationPercent = v.toInt()) } }
            // Сколько видно событие; плашки, которым нужно больше времени (сообщение с кнопками, оплата), — от него же
            GroupSlider(
                Pos.Middle, Icons.Rounded.Schedule, tr("Показ событий", "Exibição de eventos"),
                value = s.noticeDurationMs / 1000f, range = 1.5f..6f, label = tr("%.1f с", "%.1f s").format(s.noticeDurationMs / 1000f), color = Tile.Orange,
            ) { v -> SettingsRepo.update { copy(noticeDurationMs = (v * 1000).toInt()) } }
            GroupToggle(Pos.Last, Icons.Rounded.BlurOn, tr("Уменьшить эффекты", "Reduzir efeitos"),
                tr("Может быть полезно для не самых производительных устройств", "Pode ajudar em aparelhos menos potentes"),
                s.reduceEffects, color = Tile.Gray) { v -> SettingsRepo.update { copy(reduceEffects = v) } }
        }
    }

    if (styleDialog) {
        ChoiceDialog(tr("Стиль раскрытия", "Estilo de expansão"), ExpandStyle.entries, s.expandStyle, { it.title },
            { v -> SettingsRepo.update { copy(expandStyle = v) } }) { styleDialog = false }
    }
}

/** Что включено в «Эффектах» — подпись пункта. */
private fun effectsSummary(s: IslandSettings): String {
    if (s.reduceEffects) return tr("Выключены: включено «Уменьшить эффекты»", "Desligados: “Reduzir efeitos” está ligado")
    val on = listOfNotNull(
        tr("размытие", "desfoque").takeIf { s.fxBlur },
        tr("эквалайзер", "equalizador").takeIf { s.fxEqualizer },
        tr("свечение", "aura").takeIf { s.fxGlow },
        tr("волны оплаты", "ondas de pagamento").takeIf { s.fxPay },
        tr("вздрагивание", "tremor").takeIf { s.fxShake },
    )
    return if (on.isEmpty()) tr("Все выключены", "Todos desligados") else on.joinToString(", ").replaceFirstChar { it.uppercase() }
}

/** «Эффекты»: каждый живой эффект острова включается и выключается отдельно. */
@Composable
fun EffectsScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    var rateDialog by remember { mutableStateOf(false) }
    Page(tr("Эффекты", "Efeitos"), onBack, action = {
        Text(tr("Сброс", "Redefinir"), color = Ui.Sub, fontSize = 16.sp, modifier = Modifier.clickable { SettingsRepo.resetEffects() })
    }) {
        IslandPreview()
        if (s.reduceEffects) {
            Text(
                tr("Сейчас включено «Уменьшить эффекты» в «Раскрытии острова» — пока оно включено, эффектов нет",
                    "“Reduzir efeitos” está ligado em “Expansão da ilha” — enquanto estiver ligado, não há efeitos"),
                color = Tints.Orange, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp),
            )
        }

        SectionHeader(tr("Движение", "Movimento"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.BlurCircular, tr("Размытие при смене", "Desfoque na troca"),
                tr("Содержимое острова перетекает с размытием, как на iPhone (Android 12+)",
                    "O conteúdo da ilha muda com desfoque, como no iPhone (Android 12+)"),
                s.fxBlur, color = Tile.Blue) { v -> SettingsRepo.update { copy(fxBlur = v) } }
            GroupToggle(Pos.Middle, Icons.Rounded.GraphicEq, tr("Живой эквалайзер", "Equalizador animado"),
                tr("Полоски у музыки и звонка пляшут. Выключено — стоят ровно, так экономнее",
                    "As barrinhas da música e da chamada dançam. Desligado — ficam paradas, o que economiza bateria"),
                s.fxEqualizer, color = Tile.Pink) { v -> SettingsRepo.update { copy(fxEqualizer = v) } }
            // Раскрытие и смена содержимого всегда идут с частотой экрана — это только для того, что движется постоянно
            GroupItem(Pos.Middle, Icons.Rounded.Speed, tr("Частота эквалайзера", "Taxa de quadros do equalizador"),
                tr("${s.fxRate.title}. Ещё светящаяся точка фонарика и навигатора",
                    "${s.fxRate.title}. Vale também para o ponto luminoso da lanterna e da navegação"),
                color = Tile.Indigo, onClick = { rateDialog = true })
            GroupToggle(Pos.Last, Icons.Rounded.NotificationsActive, tr("Вздрагивание", "Tremor"),
                tr("Капсула вздрагивает при смене режима звука, колокольчик звонящего таймера качается",
                    "A cápsula treme quando o modo de toque muda, e o sino do timer tocando balança"),
                s.fxShake, color = Tile.Orange) { v -> SettingsRepo.update { copy(fxShake = v) } }
        }

        SectionHeader(tr("Свечение", "Aura"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.Flare, tr("Свечение обложки", "Aura da capa"),
                tr("Мягкий ореол цвета обложки вокруг острова, пока играет музыка", "Um halo suave na cor da capa ao redor da ilha enquanto a música toca"),
                s.fxGlow, color = Tile.Purple) { v -> SettingsRepo.update { copy(fxGlow = v) } }
            GroupToggle(Pos.Last, Icons.Rounded.Contactless, tr("Волны оплаты", "Ondas de pagamento"),
                tr("Волны NFC вокруг острова, пока ждём терминал, и вспышка с искрами на «Оплачено»",
                    "Ondas de NFC ao redor da ilha enquanto espera o leitor, e uma explosão de faíscas em “Pago”"),
                s.fxPay, color = Tile.Teal) { v -> SettingsRepo.update { copy(fxPay = v) } }
        }

        SectionHeader(tr("Батарея", "Bateria"))
        GroupToggle(Pos.Single, Icons.Rounded.BatterySaver, tr("Экономия заряда", "Economia de bateria"),
            tr("В режиме энергосбережения Android остров сам обходится без живых эффектов",
                "No modo de economia de bateria do Android, a ilha dispensa os efeitos animados automaticamente"),
            s.batterySaver, color = Tile.Green) { v -> SettingsRepo.update { copy(batterySaver = v) } }
    }

    if (rateDialog) {
        ChoiceDialog(tr("Частота эквалайзера", "Taxa de quadros do equalizador"), FxRate.entries, s.fxRate, { it.title },
            { v -> SettingsRepo.update { copy(fxRate = v) } }) { rateDialog = false }
    }
}
