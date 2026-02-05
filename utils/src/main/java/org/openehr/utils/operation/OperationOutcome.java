package org.openehr.utils.operation;

/**
 * Copyright 2017 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by cnanjo on 1/17/17.
 */

 /**
 * Class representing the outcome of an operation including the return value,
 * a flag indicating success or failure, and the exception thrown by the operation
 * if any.
 *
 * @deprecated This class is unused and will be removed.
 */
@Deprecated
public class OperationOutcome<T> {

    private T result;
    private OperationOutcomeStatus status;
    private Exception exception;

     /**
      * @deprecated This class is unused and will be removed.
      */
     @Deprecated
     public OperationOutcome(T result) {
         this(result, OperationOutcomeStatus.SUCCESS, null);
     }

     /**
      * @deprecated This class is unused and will be removed.
      */
     @Deprecated
     public OperationOutcome(T result, OperationOutcomeStatus status) {
         this(result, status, null);
     }

     /**
      * @deprecated This class is unused and will be removed.
      */
     @Deprecated
     public OperationOutcome(T result, OperationOutcomeStatus status, Exception exception) {
         this.result = result;
         this.status = status;
         this.exception = exception;
     }

     /**
     * The result of the operation.
     *
     * @return
     */
    public T getResult() {
        return result;
    }

    /**
     * Sets the result of the operation.
     *
     * @param result
     */
    public void setResult(T result) {
        this.result = result;
    }

    /**
     * Returns the status of the operation.
     *
     * @return
     */
    public OperationOutcomeStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the operation.
     *
     * @param status
     */
    public void setStatus(OperationOutcomeStatus status) {
        this.status = status;
    }

    /**
     * Returns the exception thrown by this operation.
     *
     * @return
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Sets the exception thrown by this operation.
     *
     * @param exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
