/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.util.infcInputStreamSource.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:58:53
 * TODO
 */
package com.jack.netty.conf.infc;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Class Name InputStreamSource
 * @Author Jack
 * @Create In 2016年8月16日
 */
public interface InputStreamSource {

    /**
     * Return an {@link InputStream}.
     * <p>It is expected that each call creates a <i>fresh</i> stream.
     * <p>This requirement is particularly important when you consider an API such
     * as JavaMail, which needs to be able to read the stream multiple times when
     * creating mail attachments. For such a use case, it is <i>required</i>
     * that each {@code getInputStream()} call returns a fresh stream.
     *
     * @return the input stream for the underlying resource (must not be {@code null})
     * @throws IOException if the stream could not be opened
     * @see org.springframework.mail.javamail.MimeMessageHelper#addAttachment(String, InputStreamSource)
     */
    InputStream getInputStream() throws IOException;
}
