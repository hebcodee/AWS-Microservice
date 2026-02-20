package com.myorg;

import software.amazon.awscdk.App;

public class AwsInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        //Vpc
        AwsVpcStack vpcStack = new AwsVpcStack(app, "Vpc");

        //Cluster
        AwsClusterStack clusterStack = new AwsClusterStack(app, "Cluster", vpcStack.getVpc());
        clusterStack.addDependency(vpcStack);

        //Service
        AwsServicesStack serviceStack = new AwsServicesStack(app, "Service", clusterStack.getCluster());
        serviceStack.addDependency(clusterStack);


        app.synth();
    }
}

