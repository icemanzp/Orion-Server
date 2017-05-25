/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.server.http.utilOpaqueUriComponents.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午10:41:37
 * TODO
 */
package com.jack.netty.server.http.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import com.jack.netty.util.LinkedMultiValueMap;
import com.jack.netty.util.ObjectUtils;
import com.jack.netty.util.infc.MultiValueMap;

/**
 * @Class Name OpaqueUriComponents
 * @Author Jack
 * @Create In 2016年8月16日
 */
@SuppressWarnings("serial")
public final class OpaqueUriComponents extends UriComponents {

    private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap<String, String>(0);

    private final String ssp;


    OpaqueUriComponents(String scheme, String schemeSpecificPart, String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }


    @Override
    public String getSchemeSpecificPart() {
        return this.ssp;
    }

    @Override
    public String getUserInfo() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public int getPort() {
        return -1;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public List<String> getPathSegments() {
        return Collections.emptyList();
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return QUERY_PARAMS_NONE;
    }

    @Override
    public UriComponents encode(String encoding) throws UnsupportedEncodingException {
        return this;
    }

    @Override
    protected UriComponents expandInternal(UriTemplateVariables uriVariables) {
        String expandedScheme = expandUriComponent(this.getScheme(), uriVariables);
        String expandedSSp = expandUriComponent(this.ssp, uriVariables);
        String expandedFragment = expandUriComponent(this.getFragment(), uriVariables);
        return new OpaqueUriComponents(expandedScheme, expandedSSp, expandedFragment);
    }

    @Override
    public UriComponents normalize() {
        return this;
    }

    @Override
    public String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();

        if (getScheme() != null) {
            uriBuilder.append(getScheme());
            uriBuilder.append(':');
        }
        if (this.ssp != null) {
            uriBuilder.append(this.ssp);
        }
        if (getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(getFragment());
        }

        return uriBuilder.toString();
    }

    @Override
    public URI toUri() {
        try {
            return new URI(getScheme(), this.ssp, getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpaqueUriComponents)) {
            return false;
        }

        OpaqueUriComponents other = (OpaqueUriComponents) obj;
        return ObjectUtils.nullSafeEquals(getScheme(), other.getScheme()) &&
                ObjectUtils.nullSafeEquals(this.ssp, other.ssp) &&
                ObjectUtils.nullSafeEquals(getFragment(), other.getFragment());

    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(getScheme());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.ssp);
        result = 31 * result + ObjectUtils.nullSafeHashCode(getFragment());
        return result;
    }


}
