package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;

/**
 * Tracks the path during validation, so string concatenation is only performed for failed validations
 */
abstract class ValidationPath {

    static ValidationPath ROOT = new ValidationPath(){
        @Override
        public String toString() {
            return "";
        }
    };
    static ValidationPath of(String path) {
        return new SimplePath(null, path, null);
    }

    private ValidationPath() {
        //NOOP
    }

    @Override
    public abstract String toString();

    ValidationPath add(String rmAttributeName) {
        return new SimplePath(this, rmAttributeName, null);
    }

    ValidationPath add(String attributeName, CObject cObject) {
        return new SimplePath(this, attributeName, cObject.getNodeId());
    }

    ValidationPath joinPaths(String other) {
        return new PathJoin(this, other);
    }

    ValidationPath stripLastPathSegment() {
        return new StripLastPathSegment(this);
    }

    private static final class SimplePath extends ValidationPath {

            private final ValidationPath parent;
            private final String path;
            private final String nodeId;

            SimplePath(ValidationPath parent, String path, String nodeId) {
                this.parent = parent;
                this.path = path;
                this.nodeId = nodeId;
            }

            @Override
            public String toString() {
                String p = path;
                if (nodeId != null) {
                    p = p + '[' + nodeId + ']';
                }
                if (parent == null) {
                    return p;
                } else {
                    return parent + "/" + p;
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

                if (path.startsWith("/") && prefix.endsWith("/")) {
                    return prefix + path.substring(1);
                } else {
                    return prefix + path;
                }
            }
        }

        private static final class StripLastPathSegment extends ValidationPath {
            private final ValidationPath child;

            StripLastPathSegment(ValidationPath child) {
                this.child = child;
            }

            @Override
            public String toString() {
                return RMObjectValidationUtil.stripLastPathSegment(child.toString());
            }
        }
    }