// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.ode;

import junit.framework.*;

import org.spaceroots.mantissa.estimation.EstimationException;
import org.spaceroots.mantissa.fitting.PolynomialFitter;

public class MidpointIntegratorTest
  extends TestCase {

  public MidpointIntegratorTest(String name) {
    super(name);
  }

  public void testDimensionCheck() {
    try  {
      TestProblem1 pb = new TestProblem1();
      new MidpointIntegrator(0.01).integrate(pb,
                                             0.0, new double[pb.getDimension()+10],
                                             1.0, new double[pb.getDimension()+10]);
        fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }
  }
  
  public void testDecreasingSteps()
    throws DerivativeException, IntegratorException  {
      
    TestProblemAbstract[] problems = TestProblemFactory.getProblems();
    for (int k = 0; k < problems.length; ++k) {

      double previousError = Double.NaN;
      for (int i = 4; i < 10; ++i) {

        TestProblemAbstract pb = (TestProblemAbstract) problems[k].clone();
        double step = (pb.getFinalTime() - pb.getInitialTime())
          * Math.pow(2.0, -i);
        FirstOrderIntegrator integ = new MidpointIntegrator(step);
        TestProblemHandler handler = new TestProblemHandler(pb);
        integ.setStepHandler(handler);
        SwitchingFunction[] functions = pb.getSwitchingFunctions();
        if (functions != null) {
          for (int l = 0; l < functions.length; ++l) {
            integ.addSwitchingFunction(functions[l],
                                       Double.POSITIVE_INFINITY, 1.0e-6 * step);
          }
        }
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        double error = handler.getMaximalError();
        if (i > 4) {
          assertTrue(error < Math.abs(previousError));
        }
        previousError = error;
      }

    }

  }

  public void testOrder()
  throws EstimationException, DerivativeException, IntegratorException {
    PolynomialFitter fitter = new PolynomialFitter(1,
                                                   10, 1.0e-7, 1.0e-10,
                                                   1.0e-10);

    TestProblemAbstract[] problems = TestProblemFactory.getProblems();
    for (int k = 0; k < problems.length; ++k) {

      for (int i = 0; i < 10; ++i) {

        TestProblemAbstract pb = (TestProblemAbstract) problems[k].clone();
        double step  = (pb.getFinalTime() - pb.getInitialTime())
          * Math.pow(2.0, -(i + 1));

        FirstOrderIntegrator integ = new MidpointIntegrator(step);
        TestProblemHandler handler = new TestProblemHandler(pb);
        integ.setStepHandler(handler);
        SwitchingFunction[] functions = pb.getSwitchingFunctions();
        if (functions != null) {
          for (int l = 0; l < functions.length; ++l) {
            integ.addSwitchingFunction(functions[l],
                                       Double.POSITIVE_INFINITY, 1.0e-6 * step);
          }
        }
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        fitter.addWeightedPair(1.0,
                               Math.log(Math.abs(step)),
                               Math.log(handler.getLastError()));

      }

      // this is an order 2 method
      double[] coeffs = fitter.fit();
      assertTrue(coeffs[1] > 1.2);
      assertTrue(coeffs[1] < 2.8);

    }

  }

  public void testSmallStep()
    throws DerivativeException, IntegratorException {

    TestProblem1 pb  = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;

    FirstOrderIntegrator integ = new MidpointIntegrator(step);
    TestProblemHandler handler = new TestProblemHandler(pb);
    integ.setStepHandler(handler);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getLastError() < 2.0e-7);
    assertTrue(handler.getMaximalError() < 1.0e-6);

  }

  public void testBigStep()
    throws DerivativeException, IntegratorException {

    TestProblem1 pb  = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

    FirstOrderIntegrator integ = new MidpointIntegrator(step);
    TestProblemHandler handler = new TestProblemHandler(pb);
    integ.setStepHandler(handler);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getLastError() > 0.01);
    assertTrue(handler.getMaximalError() > 0.05);

  }

  public static Test suite() {
    return new TestSuite(MidpointIntegratorTest.class);
  }

}
