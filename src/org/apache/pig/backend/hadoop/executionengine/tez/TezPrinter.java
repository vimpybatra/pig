/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pig.backend.hadoop.executionengine.tez;

import java.io.PrintStream;

import org.apache.pig.backend.hadoop.executionengine.physicalLayer.PhysicalOperator;
import org.apache.pig.backend.hadoop.executionengine.physicalLayer.plans.PhysicalPlan;
import org.apache.pig.backend.hadoop.executionengine.physicalLayer.plans.PlanPrinter;
import org.apache.pig.impl.plan.DependencyOrderWalker;
import org.apache.pig.impl.plan.VisitorException;

/**
 * A visitor to print out the Tez plan.
 */
public class TezPrinter extends TezOpPlanVisitor {

    private PrintStream mStream = null;
    private boolean isVerbose = true;

    /**
     * @param ps PrintStream to output plan information to
     * @param plan tez plan to print
     */
    public TezPrinter(PrintStream ps, TezOperPlan plan) {
        super(plan, new DependencyOrderWalker<TezOperator, TezOperPlan>(plan));
        mStream = ps;
    }

    public void setVerbose(boolean verbose) {
        isVerbose = verbose;
    }

    @Override
    public void visitTezOp(TezOperator tezOper) throws VisitorException {
        mStream.println("Tez vertex " + tezOper.getOperatorKey().toString());
        if (tezOper.plan != null && tezOper.plan.size() > 0) {
            PlanPrinter<PhysicalOperator, PhysicalPlan> printer =
                    new PlanPrinter<PhysicalOperator, PhysicalPlan>(tezOper.plan, mStream);
            printer.setVerbose(isVerbose);
            printer.visit();
        }
        if (tezOper.combinePlan != null && tezOper.combinePlan.size() > 0) {
            mStream.println();
            mStream.println("------------");
            mStream.println("Combine Plan");
            mStream.println("------------");
            PlanPrinter<PhysicalOperator, PhysicalPlan> printer =
                    new PlanPrinter<PhysicalOperator, PhysicalPlan>(tezOper.combinePlan, mStream);
            printer.setVerbose(isVerbose);
            printer.visit();
        }
        mStream.println();
    }
}

