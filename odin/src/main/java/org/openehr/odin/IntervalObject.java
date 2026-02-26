package org.openehr.odin;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017 Cognitive Medical Systems
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import org.antlr.v4.runtime.ParserRuleContext;

import java.io.Serializable;

/**
 * Created by cnanjo on 4/8/16.
 */
public abstract class IntervalObject<T extends PrimitiveObject, U extends ParserRuleContext> extends OdinObject implements Serializable {

    private String intervalExpression;
    private T low;
    private T high;
    private boolean excludeUpperBound;
    private boolean excludeLowerBound;

    public String getIntervalExpression() {
        return intervalExpression;
    }

    public void setIntervalExpression(String intervalExpression) {
        this.intervalExpression = intervalExpression;
    }

    public T getLow() {
        return low;
    }

    public void setLow(T low) {
        this.low = low;
    }

    public T getHigh() {
        return high;
    }

    public void setHigh(T high) {
        this.high = high;
    }

    public boolean isExcludeUpperBound() {
        return excludeUpperBound;
    }

    public void setExcludeUpperBound(boolean excludeUpperBound) {
        this.excludeUpperBound = excludeUpperBound;
    }

    public boolean isExcludeLowerBound() {
        return excludeLowerBound;
    }

    public void setExcludeLowerBound(boolean excludeLowerBound) {
        this.excludeLowerBound = excludeLowerBound;
    }

    public boolean hasOpenLowerBound() {
        return getLow() == null;
    }

    public boolean hasOpenUpperBound() {
        return getHigh() == null;
    }

    public void handleRelopExpression(ParserRuleContext ctx) {
        if(ctx.children.size() == 3) { //e.g., |3| a point interval
            setLow(extractFromContext((U)ctx.children.get(1)));
            setHigh(extractFromContext((U)ctx.children.get(1)));
            setExcludeLowerBound(false);
            setExcludeUpperBound(false);
        } else if(ctx.children.size() == 4) {
            String relops = ctx.children.get(1).getText();
            switch (relops) {
                case ">=":
                    setLow(extractFromContext((U) ctx.children.get(2)));
                    setExcludeLowerBound(false);
                    break;
                case "<=":
                    setHigh(extractFromContext((U) ctx.children.get(2)));
                    setExcludeUpperBound(false);
                    break;
                case ">":
                    setLow(extractFromContext((U) ctx.children.get(2)));
                    setExcludeLowerBound(true);
                    break;
                case "<":
                    setHigh(extractFromContext((U) ctx.children.get(2)));
                    setExcludeUpperBound(true);
                    break;
                default:
                    throw new RuntimeException("Unknown relops " + relops);
            }
        } else {
            throw new RuntimeException("Invalid number of children in parent context");
        }
    }

    public void handleRangeExpression(ParserRuleContext ctx) {
        if(ctx.children.size() == 5) { //Expression |4..5|
            setLow(extractFromContext((U)ctx.children.get(1)));
            setHigh(extractFromContext((U)ctx.children.get(3)));
        } else if(ctx.children.size() == 6) { //Expression |>4..6| or |4..<6|
            if(ctx.children.get(1).getText().equals(">")) { // |>4..6|
                setExcludeLowerBound(true);
                setExcludeUpperBound(false);
                setLow(extractFromContext((U)ctx.children.get(2)));
                setHigh(extractFromContext((U)ctx.children.get(4)));
            } else if(ctx.children.get(3).getText().equals(("<"))) { //|4..<6|
                setExcludeLowerBound(false);
                setExcludeUpperBound(true);
                setLow(extractFromContext((U)ctx.children.get(1)));
                setHigh(extractFromContext((U)ctx.children.get(4)));
            } else {
                throw new RuntimeException("Unknown grammar " + ctx.getText());
            }
        } else if(ctx.children.size() == 7) { //Expression |>4..<6|
            setExcludeLowerBound(true);
            setExcludeUpperBound(true);
            setLow(extractFromContext((U)ctx.children.get(2)));
            setHigh(extractFromContext((U)ctx.children.get(5)));
        }
    }

    protected abstract T extractFromContext(U ctx);
}
