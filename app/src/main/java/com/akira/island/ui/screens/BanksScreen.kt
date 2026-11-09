package com.akira.island.ui.screens

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.akira.island.R
import com.akira.island.core.Controls
import com.akira.island.core.IslandBus
import com.akira.island.core.tr
import com.akira.island.service.IslandTile
import com.akira.island.ui.Group
import com.akira.island.ui.GroupItem
import com.akira.island.ui.Page
import com.akira.island.ui.Perms
import com.akira.island.ui.Pos
import com.akira.island.ui.Screen
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile

/**
 * «Банк не пускает»: многие банки (K PLUS, GCash и другие) не открываются, пока включена служба спец. возможностей
 * не из Play Маркета, — так они защищаются от мошенников. Остров тут ни при чём, и обойти проверку изнутри него
 * нельзя: только выключить остров на время. Здесь — как сделать это одним нажатием и включить обратно.
 */
@Composable
fun BanksScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val running by IslandBus.serviceRunning.collectAsState()

    Page(tr("Банк не пускает", "App do banco não abre"), onBack,
        subtitle = tr("Банк пишет, что обнаружено стороннее приложение или спец. возможности?",
            "Seu banco diz que detectou um app de terceiros ou um serviço de acessibilidade?")) {
        InfoText(
            tr(
                "Так банки защищаются от мошенников: пока включена любая служба спец. возможностей не из Play Маркета, " +
                    "они не пускают. Остров ничего не читает в банке, но проверку обойти нельзя — только выключить " +
                    "остров на время. Проще всего — плиткой в шторке: нажали — остров выключился, можно в банк; " +
                    "нажали ещё раз — включаете обратно.",
                "Os bancos fazem isso para proteger contra fraudes: enquanto qualquer serviço de acessibilidade que não seja da Google Play estiver ligado, " +
                    "eles não deixam você entrar. A ilha não lê nada no seu banco, mas não há como burlar a verificação — só desligando " +
                    "a ilha por um tempo. O jeito mais fácil é um tile nas configurações rápidas: toque nele — a " +
                    "ilha desliga e você pode usar o banco; toque de novo para ligar a ilha novamente.",
            ),
        )
        Group {
            val tileAsk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            if (tileAsk) {
                GroupItem(
                    Pos.First, Icons.Rounded.AddBox, tr("Добавить плитку «Остров»", "Adicionar o tile Ilha"),
                    tr("В шторку быстрых настроек — одно нажатие", "Nas configurações rápidas — um toque"),
                    color = Tile.Blue, onClick = { addTile(context) },
                ) { Arrow() }
            }
            GroupItem(
                if (tileAsk) Pos.Middle else Pos.First, Icons.Rounded.PowerSettingsNew, tr("Выключить остров сейчас", "Desligar a ilha agora"),
                if (running) tr("Включить обратно — в спец. возможностях или плиткой", "Ligue de novo em Acessibilidade ou pelo tile")
                else tr("Остров уже выключен", "A ilha já está desligada"),
                color = Tile.Orange, onClick = { if (running) Controls.turnOffIsland() },
            )
            GroupItem(
                Pos.Last, Icons.Rounded.Lock, tr("Включить остров", "Ligar a ilha"),
                tr("Откроются спец. возможности — «Динамический остров» → включить", "Abre Acessibilidade — Ilha Dinâmica → ligar"),
                color = Tile.Green, onClick = { Perms.openAccessibility(context) },
            ) { Arrow() }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            SectionHeader(tr("Плитка в шторке", "O tile das configurações rápidas"))
            InfoText(
                tr("Откройте шторку целиком, нажмите карандаш и перетащите плитку «Остров» к остальным.",
                    "Abra as configurações rápidas por completo, toque no lápis e arraste o tile Ilha para junto dos outros."),
            )
        }
    }
}

/** Android 13+: система сама спрашивает, добавить ли плитку острова в шторку. */
private fun addTile(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val bar = context.getSystemService(StatusBarManager::class.java) ?: return
    runCatching {
        bar.requestAddTileService(
            ComponentName(context, IslandTile::class.java), context.getString(R.string.tile_label),
            Icon.createWithResource(context, R.drawable.ic_tab_island), context.mainExecutor,
        ) { result ->
            if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED) {
                Toast.makeText(context, tr("Плитка уже в шторке", "O tile já está nas configurações rápidas"), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/** Пункт «Банк не пускает» — на главной, рядом с «Чтобы остров не закрывался». */
@Composable
internal fun BanksItem(pos: Pos, open: (Screen) -> Unit) {
    GroupItem(pos, Icons.Rounded.AccountBalance, tr("Банк не пускает", "App do banco não abre"),
        tr("Плитка, чтобы выключать остров на время", "Um tile para desligar a ilha por um tempo"),
        onClick = { open(Screen.BANKS) }, color = Tile.Indigo) { Arrow() }
}
