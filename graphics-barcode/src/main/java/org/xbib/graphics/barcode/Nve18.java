package org.xbib.graphics.barcode;

import java.util.stream.IntStream;

/**
 * Calculate NVE-18 (Nummer der Versandeinheit)
 * Also SSCC-18 (Serial Shipping Container Code)
 * Encodes a 17 digit number, adding a Modulo-10 check digit.
 */
public class Nve18 extends AbstractSymbol {

    @Override
    public boolean encode() {
        StringBuilder gs1Equivalent = new StringBuilder();
        int zeroes;
        int count = 0;
        int c, cdigit;
        int p = 0;
        Code128 code128 = new Code128();

        if (content.length() > 17) {
            errorMsg.append("Input data too long");
            return false;
        }

        if (!(content.matches("[0-9]+"))) {
            errorMsg.append("Invalid characters in input");
            return false;
        }

        // Add leading zeroes
        zeroes = 17 - content.length();
        IntStream.range(0, zeroes).mapToObj(i -> "0").forEach(gs1Equivalent::append);
        gs1Equivalent.append(content);
        // Add Modulus-10 check digit
        for (int i = gs1Equivalent.length() - 1; i >= 0; i--) {
            c = Character.getNumericValue(gs1Equivalent.charAt(i));
            if ((p % 2) == 0) {
                c = c * 3;
            }
            count += c;
            p++;
        }
        cdigit = 10 - (count % 10);
        if (cdigit == 10) {
            cdigit = 0;
        }

        encodeInfo.append("NVE Check Digit: ").append(cdigit).append("\n");

        content = "[00]" + gs1Equivalent + cdigit;

        // Defer to Code 128
        code128.setDataType(SymbolDataType.GS1);
        code128.setHumanReadableLocation(getHumanReadableLocation());

        try {
            code128.setContent(content);
        } catch (Exception e) {
            errorMsg.append(e.getMessage());
            return false;
        }
        getRectangles().clear();
        getRectangles().addAll(code128.getRectangles());
        getTexts().clear();
        getTexts().addAll(code128.getTexts());
        symbolHeight = code128.symbolHeight;
        symbolWidth = code128.symbolWidth;
        encodeInfo.append(code128.encodeInfo);
        return true;
    }

    public static class Provider implements SymbolProvider<Nve18> {

        @Override
        public boolean canProvide(SymbolType type) {
            return type == SymbolType.NVE_18;
        }

        @Override
        public Nve18 provide() {
            return new Nve18();
        }
    }
}
