package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;

/**
 * Tracks the path during validation, so string concatenation is only performed for failed validations
 */
abstract class ValidationPath {

    static ValidationPath ROOT = new ValidationPath() {
        @Override
        public String toString() {
            return "";
        }
    };

    private ValidationPath() {
        //NOOP
    }

    @Override
    public abstract String toString();

    ValidationPath add(String rmAttributeName) {
        return new NodeSegmentAppend(this, rmAttributeName, null);
    }

    ValidationPath add(String attributeName, CObject cObject) {
        return new NodeSegmentAppend(this, attributeName, cObject.getNodeId());
    }

    ValidationPath joinPaths(String other) {
        return new PathJoin(this, other);
    }

    ValidationPath stripLastPathSegment() {
        return new StripLastPathSegment(this);
    }

    private static final class NodeSegmentAppend extends ValidationPath {

        private final ValidationPath parent;
        private final String attribute;
        private final String nodeId;

        NodeSegmentAppend(ValidationPath parent, String attribute, String nodeId) {
            this.parent = parent;
            this.attribute = attribute;
            this.nodeId = nodeId;
        }

        @Override
        public String toString() {
            String p = attribute;
            if (nodeId != null) {
                p = p + '[' + nodeId + ']';
            }
            if (parent == null) {
                return p;
            } else {
                String parentStr =  parent.toString();
                if (parentStr.endsWith("/")) {
                    return parent +  p;
                } else {
                    return parent + "/" + p;
                }
            }
        }
    }

    private static final class PathJoin extends ValidationPath {
        private final ValidationPath parent;
        private final String path;

        PathJoin(ValidationPath parent, String path) {
            this.parent = parent;
            this.path = path;
        }

        @Override
        public String toString() {
            String prefix = parent.toString();
            if (prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            String postfix = path.startsWith("/") ? path.substring(1) : path;

            if (postfix.isEmpty()) {
                if (prefix.isEmpty()) {
                    return "/";
                } else {
                    return prefix;
                }
            } else {
                return prefix + '/' + postfix;
            }
        }
    }

    private static final class StripLastPathSegment extends ValidationPath {
        private final ValidationPath fullPath;

        StripLastPathSegment(ValidationPath fullPath) {
            this.fullPath = fullPath;
        }

        @Override
        public String toString() {
            return RMObjectValidationUtil.stripLastPathSegment(fullPath.toString());
        }
    }
}