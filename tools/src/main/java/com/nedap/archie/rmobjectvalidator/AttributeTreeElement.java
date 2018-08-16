package com.nedap.archie.rmobjectvalidator;

import com.google.common.collect.Ordering;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.paths.PathSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 02/02/16.
 */
public class AttributeTreeElement {

    private final String attributeName;
    private final IDTreeElement parent;
    private List<IDTreeElement> idTreeElements = new ArrayList<>();

    private Object value;

    public AttributeTreeElement(String attributeName, IDTreeElement parent) {
        this.attributeName = attributeName;
        this.parent = parent;
    }

    /**
     * Add an id tree element at the given index in the list
     */
    public IDTreeElement addIdTreeElement(Integer index, String id) {
        IDTreeElement element = findByIdAndIndex(id, index);
        if (element == null) {
            element = new IDTreeElement(id, index, this);
            idTreeElements.add(element);
        }
        return element;

    }

    private IDTreeElement findByIdAndIndex(String id, Integer index) {
        for (IDTreeElement element : idTreeElements) {
            if (Objects.equals(element.getId(), id) && Objects.equals(element.getOneBasedIndex(), index)) {
                return element;
            } else if ((element.getId() == null || id == null) && Objects.equals(element.getOneBasedIndex(), index)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Order the ID-tree elements. First put the ones with an explicit index at the correct position
     * Then order by id alphabetically, and fill in the rest.
     * So we can support all kinds of input.
     * TODO: this is backwards compatibility code. Once the UI does this correctly, throw this code away or seriously refactor
     */
    protected void orderIdTreeElements() {

        Ordering<IDTreeElement> ordering = Ordering.natural()
                .nullsLast()
                .<IDTreeElement>onResultOf(element -> element.getOneBasedIndex());
        Collections.sort(idTreeElements, ordering);

        List<IDTreeElement> elementsWithIndex = new ArrayList<>(idTreeElements.size());
        List<IDTreeElement> elementsWithoutIndex = new ArrayList<>(idTreeElements.size());
        for (IDTreeElement element : idTreeElements) {
            if (element.getOneBasedIndex() != null) {
                elementsWithIndex.add(element);
            } else {
                elementsWithoutIndex.add(element);
            }
        }

        removeIndexGaps(elementsWithIndex);
        //fixDuplicateIndexes(elementsWithIndex);


        ArrayList<IDTreeElement> elements = new ArrayList<>(Collections.nCopies(idTreeElements.size(), null));
        for (IDTreeElement element : elementsWithIndex) {
            int index = element.getZeroBasedIndex();
            elements.set(index, element);//xpath indexes are 1-based, so subtract here
        }

        Collections.sort(elementsWithoutIndex, ordering);

        int index = 0;
        for (IDTreeElement element : elementsWithoutIndex) {
            while (elements.get(index) != null) {
                index++;
            }
            elements.set(index, element);
            index = index + 1;
        }
        idTreeElements = elements;
    }


// attempt to fix a nasty bug, may be needed again in the future.
// occurred when two elements have been commited, one with id and index one with just the same index
// it left a gap in the list, so a null element, which crashed things later on.
// now fixed by allowing matching without id so it works
// it's still possible that due to a mismatch in chiron and the UI gaps (null elements) occur in this list
// this function fixes the gaps.
//    private void fixDuplicateIndexes(List<IDTreeElement> elementsWithIndex) {
//        if(elementsWithIndex.isEmpty()) {
//            return;
//        }
//        Integer highestIndex = elementsWithIndex.stream().map( e -> e.getOneBasedIndex()).max(Comparator.naturalOrder()).get();
//        for(IDTreeElement element:elementsWithIndex) {
//            if(elementsWithIndex.stream().filter( element2 -> element.getOneBasedIndex().equals(element2.getOneBasedIndex())).count() > 1) {
//                element.setOneBasedIndex(highestIndex + 1);
//                highestIndex = highestIndex + 1;
//            }
//        }
//    }

    /**
     * If someone used an index that does not fit in the list, set it to a proper index
     *
     * @param elementsWithIndex
     */
    private void removeIndexGaps(List<IDTreeElement> elementsWithIndex) {
        for (IDTreeElement element : elementsWithIndex) {
            if (element.getZeroBasedIndex() > idTreeElements.size() - 1) {
                int indexToBeUsed = 1;
                while (isUsed(indexToBeUsed, elementsWithIndex)) {
                    indexToBeUsed++;
                }
                element.setOneBasedIndex(indexToBeUsed);
                //index out of bounds. Reset indices
            }
        }
    }

    private boolean isUsed(int indexToBeUsed, List<IDTreeElement> elementsWithIndex) {
        for (IDTreeElement element : elementsWithIndex) {
            if (element.getOneBasedIndex().intValue() == indexToBeUsed) {
                return true;
            }
        }
        return false;
    }

    public List<IDTreeElement> getIdTreeElements() {
        return idTreeElements;
    }

    public List<IDTreeElement> getIdTreeElementsById(String nodeId) {
        List<IDTreeElement> result = new ArrayList<>();
        for (IDTreeElement element : idTreeElements) {
            String elementId = element.getId();
            addElementsById(nodeId, result, element, elementId);
        }
        return result;
    }

    public List<IDTreeElement> getIdTreeElementsByIdOrWithoutId(String nodeId) {

        List<IDTreeElement> result = new ArrayList<>();
        for (IDTreeElement element : idTreeElements) {
            String elementId = element.getId();

            if (elementId == null || Objects.equals(nodeId, CPrimitiveObject.PRIMITIVE_NODE_ID_VALUE)) {
                result.add(element);
            } else {
                addElementsById(nodeId, result, element, elementId);
            }
        }
        return result;
    }

    private void addElementsById(String nodeId, List<IDTreeElement> result, IDTreeElement element, String elementId) {
        int dotIndex = elementId.indexOf('.');
        String elementIdWithoutDots = elementId;
        if (elementId.startsWith("id") && dotIndex > -1) {//if parameter is id102.1, also match id102
            elementIdWithoutDots = elementId.substring(0, dotIndex);
        }
        if (Objects.equals(elementId, nodeId) || Objects.equals(elementIdWithoutDots, nodeId)) {
            result.add(element);
        }
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean hasValue() {
        return value != null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public IDTreeElement getParent() {
        return parent;
    }

    public String reconstructPath() {
        return reconstructPathInner().toString();
    }

    private StringBuilder reconstructPathInner() {
        IDTreeElement parent = getParent();
        if (parent == null) return new StringBuilder(getAttributeName());

        String pathSegment = new PathSegment(
                getAttributeName(),
                null,
                null
        ).toString();

        return parent.reconstructPathInner().append(pathSegment);
    }

}
