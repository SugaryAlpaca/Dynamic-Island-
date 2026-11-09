package com.akira.island.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.island.core.tr
import com.akira.island.data.SettingsRepo
import com.akira.island.ui.Group
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.Page
import com.akira.island.ui.Pos
import com.akira.island.ui.Tile
import com.akira.island.ui.Ui

@Composable
fun LyricsScreen(onBack: () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    Page(tr("Текст песен", "Letra"), onBack) {
        Group {
            GroupToggle(Pos.Single, Icons.Rounded.LibraryMusic, tr("Текст песен", "Letra"),
                tr("Строка песни в раскрытом плеере заливается белым по мере пения", "A linha atual no player expandido é preenchida de branco conforme a música é cantada"),
                s.lyricsEnabled, color = Tile.Pink) { v -> SettingsRepo.update { copy(lyricsEnabled = v) } }
        }
        Text(
            tr(
                "Текст ищется по названию, исполнителю и длительности трека: сначала на LRCLIB (lrclib.net), " +
                    "потом на NetEase — там много русской музыки. Если синхронного текста нет нигде, строки обычного текста " +
                    "раскладываются по длине песни. Пока идёт поиск, в плеере написано «Ищу текст песни…», " +
                    "если ничего не нашлось — «Текст песни не найден». На сервер уходят только название и исполнитель. " +
                    "Работает с любым плеером, который показывает уведомление с управлением: Spotify, YouTube Music, Яндекс Музыка, VK и т.д.",
                "A letra é buscada pelo título, artista e duração da faixa: primeiro no LRCLIB (lrclib.net), depois no NetEase, " +
                    "que tem muita música russa. Se não houver letra sincronizada em lugar nenhum, as linhas da letra simples são distribuídas ao longo " +
                    "da música. Durante a busca, o player mostra “Procurando a letra…”, e se nada for encontrado —" +
                    " “Letra não encontrada”. Só o título e o artista são enviados ao servidor. Funciona com qualquer player que mostre uma " +
                    "notificação com controles: Spotify, YouTube Music, Yandex Music, VK e outros.",
            ),
            color = Ui.Sub,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Ui.Card).padding(20.dp),
        )
    }
}
