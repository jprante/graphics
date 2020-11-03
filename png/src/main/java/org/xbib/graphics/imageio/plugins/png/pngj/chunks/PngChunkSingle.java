package org.xbib.graphics.imageio.plugins.png.pngj.chunks;

import org.xbib.graphics.imageio.plugins.png.pngj.ImageInfo;

/**
 * PNG chunk type (abstract) that does not allow multiple instances in same
 * image.
 */
public abstract class PngChunkSingle extends PngChunk {

    protected PngChunkSingle(String id, ImageInfo imgInfo) {
        super(id, imgInfo);
    }

    public final boolean allowsMultiple() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
        PngChunkSingle other = (PngChunkSingle) obj;
        if (id == null) {
			return other.id == null;
        } else return id.equals(other.id);
	}

}
