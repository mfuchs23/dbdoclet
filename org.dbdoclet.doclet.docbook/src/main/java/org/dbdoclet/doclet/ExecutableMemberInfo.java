package org.dbdoclet.doclet;

import static java.util.Objects.requireNonNull;

import javax.lang.model.element.ExecutableElement;

import org.dbdoclet.doclet.doc.DocManager;


public class ExecutableMemberInfo {

    private ExecutableElement method;
    private ExecutableElement implemented;
    private ExecutableElement overridden;

    public ExecutableMemberInfo(ExecutableElement method) {
    	requireNonNull(method, "The argument method must not be null!");
        this.method = method;
    }

    public void setExecutableMember(ExecutableElement method) {
    	requireNonNull(method, "The argument method must not be null!");
        this.method = method;
    }

    public ExecutableElement getExecutableMember() {
        return method;
    }

    public void setImplemented(ExecutableElement implemented) {
        this.implemented = implemented;
    }

    public ExecutableElement getImplemented() {
        return implemented;
    }

    public void setOverridden(ExecutableElement overridden) {
        this.overridden = overridden;
    }

    public ExecutableElement getOverridden() {
        return overridden;
    }

    public ExecutableElement getCommentDoc() {

    	requireNonNull(method, "The field method must not be null!");

    	DocManager docManager = CDI.getInstance(DocManager.class);
        String comment = docManager.getCommentText(method);

        if ((comment != null) && (comment.trim().length() > 0)) {
            return method;
        } else {
            if (implemented != null) {
                comment = docManager.getCommentText(implemented);
                if ((comment != null) && (comment.trim().length() > 0)) {
                    return implemented;
                }
            }
        }

        return method;
    }
}
