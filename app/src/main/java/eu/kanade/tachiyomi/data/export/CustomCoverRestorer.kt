package eu.kanade.tachiyomi.data.export

import android.content.Context
import android.net.Uri
import eu.kanade.tachiyomi.data.backup.models.BackupCovers
import eu.kanade.tachiyomi.data.cache.CoverCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import tachiyomi.domain.manga.interactor.GetFavorites
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.zip.ZipInputStream

object CustomCoverRestorer {

    suspend fun restoreFromZip(
        context: Context,
        uri: Uri,
        onRestoreComplete: () -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    ZipInputStream(inputStream).use { zipInputStream ->
                        var entry = zipInputStream.nextEntry
                        var protoData: ByteArray? = null
                        val imageMap = mutableMapOf<String, ByteArray>()

                        while (entry != null) {
                            if (entry.name == "manga_urls.proto") {
                                protoData = zipInputStream.readBytes()
                            } else if (entry.name.endsWith(".jpg")) {
                                val imageData = zipInputStream.readBytes()
                                imageMap[entry.name] = imageData
                            }
                            zipInputStream.closeEntry()
                            entry = zipInputStream.nextEntry
                        }

                        if (protoData != null) {
                            val backupCoverMappings = ProtoBuf.decodeFromByteArray<BackupCovers>(protoData)
                            val mangas = Injekt.get<GetFavorites>().await()
                            val coverCache = Injekt.get<CoverCache>()

                            backupCoverMappings().forEach { mapping ->
                                val matchingManga = mangas.find {
                                    it.url == mapping.mangaUrl && it.source == mapping.sourceId
                                }

                                if (matchingManga != null) {
                                    val imageName = "${mapping.filename}.jpg"
                                    val coverData = imageMap[imageName]

                                    if (coverData != null) {
                                        val coverInputStream = coverData.inputStream()
                                        coverCache.setCustomCoverToCache(matchingManga, coverInputStream)
                                    }
                                }
                            }
                        }
                    }
                }

                onRestoreComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
