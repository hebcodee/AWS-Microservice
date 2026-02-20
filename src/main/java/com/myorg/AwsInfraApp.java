package com.myorg;

import software.amazon.awscdk.App;

public class AwsInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        //Vpc
        AwsVpcStack vpcStack = new AwsVpcStack(app, "Vpc");

        //Cluster
        new AwsClusterStack(app, "Cluster", vpcStack.getVpc())
                .addDependency(vpcStack);

        app.synth();
    }
}

