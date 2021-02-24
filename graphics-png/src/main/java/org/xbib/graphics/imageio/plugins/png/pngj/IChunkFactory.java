package org.xbib.graphics.imageio.plugins.png.pngj;

import org.xbib.graphics.imageio.plugins.png.pngj.chunks.ChunkRaw;
import org.xbib.graphics.imageio.plugins.png.pngj.chunks.PngChunk;

/**
 * Factory to create a {@link PngChunk} from a {@link ChunkRaw}.
 * <p>
 * Used by {@link PngReader}
 */
public interface IChunkFactory {

    /**
     * @param chunkRaw Chunk in raw form. Data can be null if it was skipped or
     *                 processed directly (eg IDAT)
     * @param imgInfo  Not normally necessary, but some chunks want this info
     * @return should never return null.
     */
	PngChunk createChunk(ChunkRaw chunkRaw, ImageInfo imgInfo);

}
