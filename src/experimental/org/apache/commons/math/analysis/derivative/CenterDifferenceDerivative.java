/*
 * 
 * Copyright (c) 2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */

package org.apache.commons.math.analysis.derivative;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * @todo add javadoc comment
 * @version $Revision: 1.3 $ $Date: 2004/01/29 16:54:04 $
 */
public class CenterDifferenceDerivative extends AbstractDifferenceDerivative {
    
    /**
     * @todo add javadoc comment
     */
    public CenterDifferenceDerivative(UnivariateRealFunction function, double h) {
        super(function, h);
    }
    
    /**
     * @todo add javadoc comment
     */
    public double value(double x) throws MathException {
        UnivariateRealFunction f = getFunction();
        double h2 = getDelta();
        double h = h2 * .5;
        return (f.value(x + h) - f.value(x - h)) / h2;
    } 
    
    /**
     * 
     */
    public static UnivariateRealFunction decorate(UnivariateRealFunction function, double h) {
        return new CenterDifferenceDerivative(function, h);
    }
}
