package org.xbib.graphics.ghostscript;

import javax.print.PrintService;
import javax.print.attribute.standard.Media;

import java.util.HashMap;
import java.util.Map;

public class Printer {

    public static final int MODE_MONOCHROME = 0;
    public static final int MODE_COLOR = 1;
    public static final int DUPLEX_SIMPLEX = 0;
    public static final int DUPLEX_HORIZONTAL = 1;
    public static final int DUPLEX_VERTICAL = 2;
    public static final int STATUS_ACCEPTING_JOBS = 0;
    public static final int STATUS_NOT_ACCEPTING_JOBS = 1;
    private String name;
    private int status;
    private String model;
    private String info;
    private boolean copiesSupported;
    private int copies;
    private boolean collateSupported;
    private boolean collate;
    private boolean modeSupported;
    private int mode;
    private boolean duplexSupported;
    private int duplex;
    private boolean mediaSupported;
    private String mediaSize;
    private Map<String, Media> mediaSizeNames = new HashMap<>();
    private int pageSize;
    private String pageRange;
    private PrintService service;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isCopiesSupported() {
        return copiesSupported;
    }

    public void setCopiesSupported(boolean copiesSupported) {
        this.copiesSupported = copiesSupported;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public boolean isCollateSupported() {
        return collateSupported;
    }

    public void setCollateSupported(boolean collateSupported) {
        this.collateSupported = collateSupported;
    }

    public boolean isCollate() {
        return collate;
    }

    public void setCollate(boolean collate) {
        this.collate = collate;
    }

    public boolean isModeSupported() {
        return modeSupported;
    }

    public void setModeSupported(boolean modeSupported) {
        this.modeSupported = modeSupported;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isDuplexSupported() {
        return duplexSupported;
    }

    public void setDuplexSupported(boolean duplexSupported) {
        this.duplexSupported = duplexSupported;
    }

    public int getDuplex() {
        return duplex;
    }

    public void setDuplex(int duplex) {
        this.duplex = duplex;
    }

    public boolean isMediaSupported() {
        return mediaSupported;
    }

    public void setMediaSupported(boolean mediaSupported) {
        this.mediaSupported = mediaSupported;
    }

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }

    public Map<String, Media> getMediaSizeNames() {
        return mediaSizeNames;
    }

    public void addMediaSizeName(String name, Media media) {
        mediaSizeNames.put(name, media);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageRange() {
        return pageRange;
    }

    public void setPageRange(String pageRange) {
        this.pageRange = pageRange;
    }

    public PrintService getService() {
        return service;
    }

    public void setService(PrintService service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return name;
    }
}
