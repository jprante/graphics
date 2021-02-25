package org.xbib.graphics.pdfbox.groovy.factory

import org.xbib.graphics.pdfbox.groovy.Image
import org.xbib.graphics.pdfbox.groovy.ImageType
import org.xbib.graphics.pdfbox.groovy.TextBlock

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.security.MessageDigest

class ImageFactory extends AbstractFactory {

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Image image = new Image(attributes)
        if (!image.width || !image.height) {
            InputStream inputStream = new ByteArrayInputStream(image.data)
            // TODO add TwelveMonkeys
            BufferedImage bufferedImage = ImageIO.read(inputStream)
            image.width = bufferedImage.width
            image.height = bufferedImage.height
            bufferedImage.getType()
        }
        if (!image.name || builder.imageFileNames.contains(image.name)) {
            image.name = generateImageName(image)
        }
        builder.imageFileNames << image.name
        TextBlock paragraph
        if (builder.parentName == 'paragraph') {
            paragraph = builder.current as TextBlock
        } else {
            paragraph = builder.getColumnParagraph(builder.current)
        }
        image.parent = paragraph
        paragraph.children << image
        if (!image.type) {
            String suffix = suffixOf(image.name)
            switch (suffix) {
                case 'bmp':
                    image.type = ImageType.BMP
                    break
                case 'gif':
                    image.type = ImageType.GIF
                    break
                case 'jpg':
                case 'jpeg':
                    image.type = ImageType.JPG
                    break
                case 'png':
                    image.type = ImageType.PNG
                    break
                case 'tif':
                case 'tiff':
                    image.type = ImageType.TIF
                    break
                default:
                    image.type = ImageType.PNG
                    break
            }
        }
        image
    }

    String generateImageName(Image image) {
        if (!image) {
            throw new IllegalArgumentException('no image')
        }
        Formatter hexHash = new Formatter()
        MessageDigest.getInstance('SHA-256').digest(image.data).each { b ->
            hexHash.format('%02x', b)
        }
        String type = ''
        if (image.type) {
            switch (image.type) {
                case ImageType.BMP:
                    type = 'bmp'
                    break
                case ImageType.GIF:
                    type = 'gif'
                    break
                case ImageType.JPG:
                    type = 'jpg'
                    break
                case ImageType.PNG:
                    type = 'png'
                    break
                case ImageType.TIF:
                    type = 'tif'
                    break
                default:
                    type = 'png'
            }
        }
        "${hexHash}.${type}"
    }

    @Override
    boolean isLeaf() {
        true
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, node, Map attributes) {
        false
    }

    static String suffixOf(String filename) {
        int pos = filename.lastIndexOf('.')
        return pos > 0 ? filename.substring(pos + 1).toLowerCase(Locale.ROOT) : null
    }
}
