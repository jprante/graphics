import org.xbib.graphics.barcode.AustraliaPost;
import org.xbib.graphics.barcode.AztecCode;
import org.xbib.graphics.barcode.AztecRune;
import org.xbib.graphics.barcode.ChannelCode;
import org.xbib.graphics.barcode.Codabar;
import org.xbib.graphics.barcode.CodablockF;
import org.xbib.graphics.barcode.Code11;
import org.xbib.graphics.barcode.Code128;
import org.xbib.graphics.barcode.Code16k;
import org.xbib.graphics.barcode.Code2Of5;
import org.xbib.graphics.barcode.Code32;
import org.xbib.graphics.barcode.Code3Of9;
import org.xbib.graphics.barcode.Code3Of9Extended;
import org.xbib.graphics.barcode.Code49;
import org.xbib.graphics.barcode.Code93;
import org.xbib.graphics.barcode.CodeOne;
import org.xbib.graphics.barcode.Composite;
import org.xbib.graphics.barcode.DataBar14;
import org.xbib.graphics.barcode.DataBarExpanded;
import org.xbib.graphics.barcode.DataBarLimited;
import org.xbib.graphics.barcode.DataMatrix;
import org.xbib.graphics.barcode.Ean;
import org.xbib.graphics.barcode.GridMatrix;
import org.xbib.graphics.barcode.JapanPost;
import org.xbib.graphics.barcode.KixCode;
import org.xbib.graphics.barcode.KoreaPost;
import org.xbib.graphics.barcode.Logmars;
import org.xbib.graphics.barcode.MaxiCode;
import org.xbib.graphics.barcode.MicroQrCode;
import org.xbib.graphics.barcode.MsiPlessey;
import org.xbib.graphics.barcode.Nve18;
import org.xbib.graphics.barcode.Pdf417;
import org.xbib.graphics.barcode.Pharmacode;
import org.xbib.graphics.barcode.Pharmacode2Track;
import org.xbib.graphics.barcode.Pharmazentralnummer;
import org.xbib.graphics.barcode.Postnet;
import org.xbib.graphics.barcode.QrCode;
import org.xbib.graphics.barcode.RoyalMail4State;
import org.xbib.graphics.barcode.SymbolProvider;
import org.xbib.graphics.barcode.Telepen;
import org.xbib.graphics.barcode.Upc;
import org.xbib.graphics.barcode.UspsOneCode;
import org.xbib.graphics.barcode.UspsPackage;

module org.xbib.graphics.barcode {
    exports org.xbib.graphics.barcode;
    exports org.xbib.graphics.barcode.util;
    exports org.xbib.graphics.barcode.render;
    requires transitive java.desktop;
    provides SymbolProvider with
            AustraliaPost.Provider,
            AztecCode.Provider,
            AztecRune.Provider,
            ChannelCode.Provider,
            Codabar.Provider,
            CodablockF.Provider,
            Code2Of5.Provider,
            Code3Of9.Provider,
            Code3Of9Extended.Provider,
            Code11.Provider,
            Code16k.Provider,
            Code32.Provider,
            Code49.Provider,
            Code93.Provider,
            Code128.Provider,
            CodeOne.Provider,
            Composite.Provider,
            DataBar14.Provider,
            DataBarExpanded.Provider,
            DataBarLimited.Provider,
            DataMatrix.Provider,
            Ean.Provider,
            GridMatrix.Provider,
            JapanPost.Provider,
            KixCode.Provider,
            KoreaPost.Provider,
            Logmars.Provider,
            MaxiCode.Provider,
            MicroQrCode.Provider,
            MsiPlessey.Provider,
            Nve18.Provider,
            Pdf417.Provider,
            Pharmacode.Provider,
            Pharmacode2Track.Provider,
            Pharmazentralnummer.Provider,
            Postnet.Provider,
            QrCode.Provider,
            RoyalMail4State.Provider,
            Telepen.Provider,
            Upc.Provider,
            UspsOneCode.Provider,
            UspsPackage.Provider;
}
