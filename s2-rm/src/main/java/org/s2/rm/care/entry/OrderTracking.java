package org.s2.rm.care.entry;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.patterns.data_structures.Node;

/**
* BMM name: Order_tracking
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order_tracking", propOrder = {
  "orderId",
  "requestorId",
  "fillerId",
  "details"
})
public class OrderTracking {
  /**
  * BMM name: order_id | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "order_id")
  private String orderId;

  /**
  * BMM name: requestor_id | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "requestor_id")
  private @Nullable String requestorId;

  /**
  * BMM name: filler_id | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "filler_id")
  private @Nullable String fillerId;

  /**
  * BMM name: details | BMM type: List<Node>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "details")
  private @Nullable List<Node> details;

  public OrderTracking() {}

  public OrderTracking(String orderId) {
    this.orderId = orderId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    OrderTracking otherAsOrderTracking = (OrderTracking) other;
    return Objects.equals(orderId, otherAsOrderTracking.orderId) &&
      Objects.equals(requestorId, otherAsOrderTracking.requestorId) &&
      Objects.equals(fillerId, otherAsOrderTracking.fillerId) &&
      Objects.equals(details, otherAsOrderTracking.details);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), orderId, requestorId, fillerId);
    result = details == null ? 0 : 31 * result + details.hashCode();
    return result;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public @Nullable String getRequestorId() {
    return requestorId;
  }

  public void setRequestorId(@Nullable String requestorId) {
    this.requestorId = requestorId;
  }

  public @Nullable String getFillerId() {
    return fillerId;
  }

  public void setFillerId(@Nullable String fillerId) {
    this.fillerId = fillerId;
  }

  public @Nullable List<Node> getDetails() {
    return details;
  }

  public void setDetails(@Nullable List<Node> details) {
    this.details = details;
  }

  public String bmmClassName() {
    return "Order_tracking";
  }

  @Override
  public String toString() {
    return "Order_tracking";
  }
}
