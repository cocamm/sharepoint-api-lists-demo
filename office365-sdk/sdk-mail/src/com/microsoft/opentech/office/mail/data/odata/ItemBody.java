package com.microsoft.opentech.office.mail.data.odata;

import java.io.Serializable;

/**
 * Represents ItemBody odata type.
 */
public class ItemBody implements Serializable {
    
    /**
     * Item body type.
     */
    public enum BodyType {
        
        /**
         * Plain text body type.
         */
        TEXT,
        
        /**
         * HTML body type.
         */
        HTML
    }

    /**
     * Body content type.
     */
    private BodyType mContentType;
    
    /**
     * Body content.
     */
    private String mContent;
    
    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 4L;
    
    /**
     * Gets current {@link ItemBody} instance content type.
     * 
     * @return Content type.
     */
    public BodyType getContentType() {
        return mContentType;
    }
    
    /**
     * Sets current {@link ItemBody} instance content type.
     * 
     * @param contentType New content type value.
     * @return Current {@link ItemBody} instance.
     */
    public ItemBody setContentType(BodyType contentType) {
        this.mContentType = contentType;
        return this;
    }
    
    /**
     * Gets current {@link ItemBody} content.
     * 
     * @return Content.
     */
    public String getContent() {
        return mContent;
    }
    
    /**
     * Sets current {@link ItemBody} content.
     * @param content New content.
     * @return Current {@link ItemBody} instance.
     */
    public ItemBody setContent(String content) {
        this.mContent = content;
        return this;
    }
}