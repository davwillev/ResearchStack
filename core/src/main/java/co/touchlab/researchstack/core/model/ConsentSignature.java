package co.touchlab.researchstack.core.model;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.UUID;

//TODO Remove/Rewrite all documentation
public class ConsentSignature implements Serializable, Cloneable
{

    /**
     * A Boolean value indicating whether the user needs to enter their name during consent review.
     *
     * The default value of this property is `YES`. In a consent review step, the name entry screen is not displayed when the value of this property is `NO`.
     */
    private boolean requiresName;

    /**
     * A Boolean value indicating whether the user needs to draw a signature during consent review.
     *
     * The default value of this property is `YES`. In a consent review step, the signature entry
     * screen is not shown when this property is `NO`.
     */
   private boolean requiresSignatureImage;

    /**
     * The identifier for this signature.
     *
     * The identifier should be unique in the document. It can be used to find or
     * replace a specific signature in an `ORKConsentDocument` object. The identifier is also reproduced in
     * the `ORKConsentSignatureResult` object produced by an `ORKConsentReviewStep` object.
     */
   private String identifier;

    /**
     * The title of the signatory.
     */
    private String title;

    /**
     * Full name
     */
    private String fullName;

    /**
     * The base64-encoded image of the signature, if any.
     */
    private String signatureImage;

    /**
     * The date associated with the signature.
     */
    private String signatureDate;

    /**
     The date format string to be used when producing a date string for the PDF
     or consent review.

     For example, @"yyyy-MM-dd 'at' HH:mm". When the value of this property is `nil`,
     the current date and time for the current locale is used.
     */
    private String signatureDateFormatString;

    public ConsentSignature(){
        this.requiresName = true;
        this.requiresSignatureImage = true;
        this.identifier = UUID.randomUUID().toString();
    }

    /**
     * TODO Whats the point of marking params nullable even the first three params, specifically dateFormat, can be null as well
     *
     * @param title               The title of the signatory.
     * @param dateFormat          The format string to use when formatting the date of signature.
     * @param identifier          The identifier of the signature, unique within this document.
     * @param fullName           The given name of the signatory.
     * @param signatureImage      An image of the signature.
     * @param signatureDate       The date on which the signature was obtained, represented as a string.
     */
    public ConsentSignature(String identifier, String title, String dateFormat, @Nullable String fullName, @Nullable String signatureImage, @Nullable String signatureDate)
    {
        this();
        this.identifier = identifier;
        this.title = title;
        this.signatureDateFormatString = dateFormat;
        this.fullName = fullName;
        this.signatureImage = signatureImage;
        this.signatureDate = signatureDate;
    }

    /**
     *
     * @param title               The title of the signatory.
     * @param dateFormat    The format string to use when formatting the date of signature.
     * @param identifier          The identifier of the signature, unique within this document.
     */
    public ConsentSignature(String identifier, String title, String dateFormat)
    {
        this(identifier, title, dateFormat, null, null, null);
    }

    public void setIdentifier(String identifier)
    {
        if (TextUtils.isEmpty(identifier))
        {
            throw new IllegalStateException("Identifier cannot be null");
        }

        this.identifier = identifier;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public boolean isRequiresName()
    {
        return requiresName;
    }

    public void setRequiresName(boolean requiresName)
    {
        this.requiresName = requiresName;
    }

    public boolean isRequiresSignatureImage()
    {
        return requiresSignatureImage;
    }

    public void setRequiresSignatureImage(boolean requiresSignatureImage)
    {
        this.requiresSignatureImage = requiresSignatureImage;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof ConsentSignature &&
                ((ConsentSignature) o).identifier.equals(identifier) &&
                ((ConsentSignature) o).title.equals(title) &&
                ((ConsentSignature) o).fullName.equals(fullName) &&
                ((ConsentSignature) o).signatureDate.equals(signatureDate) &&
                ((ConsentSignature) o).signatureImage.equals(signatureImage) &&
                ((ConsentSignature) o).signatureDateFormatString.equals(signatureDateFormatString) &&
                ((ConsentSignature) o).requiresName == requiresName &&
                ((ConsentSignature) o).requiresSignatureImage == requiresSignatureImage;
    }

    public String getTitle()
    {
        return title;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setSignatureImage(String signatureImage)
    {
        this.signatureImage = signatureImage;
    }

    public String getSignatureImage()
    {
        return signatureImage;
    }

    public String getSignatureDate()
    {
        return signatureDate;
    }

    public String getSignatureDateFormatString()
    {
        return signatureDateFormatString;
    }

}
