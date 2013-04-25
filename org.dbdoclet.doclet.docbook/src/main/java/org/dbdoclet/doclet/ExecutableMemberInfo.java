package org.dbdoclet.doclet;

import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;


public class ExecutableMemberInfo {

    private ExecutableMemberDoc method;
    private MethodDoc implemented;
    private MethodDoc overridden;

    public ExecutableMemberInfo(ExecutableMemberDoc method) {

        if (method == null) {

            throw new IllegalArgumentException(
                "The argument method must not be null!");
        }

        this.method = method;
    }

    public void setExecutableMember(ExecutableMemberDoc method) {

        if (method == null) {

            throw new IllegalArgumentException(
                "The argument method must not be null!");
        }

        this.method = method;
    }

    public ExecutableMemberDoc getExecutableMember() {

        return method;
    }

    public void setImplemented(MethodDoc implemented) {

        this.implemented = implemented;
    }

    public MethodDoc getImplemented() {

        return implemented;
    }

    public void setOverridden(MethodDoc overridden) {

        this.overridden = overridden;
    }

    public MethodDoc getOverridden() {

        return overridden;
    }

    public ExecutableMemberDoc getCommentDoc() {

        if (method == null) {

            throw new IllegalStateException("The field method must not be null!");
        }

        String comment = method.commentText();

        if ((comment != null) && (comment.trim().length() > 0)) {

            return method;
        } else {

            if (implemented != null) {

                comment = implemented.commentText();

                if ((comment != null) && (comment.trim().length() > 0)) {

                    return implemented;
                }
            }
        }

        return method;
    }
}
