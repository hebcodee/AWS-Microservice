package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;


public class AwsVpcStack extends Stack {

    private Vpc vpc;

    public AwsVpcStack(final Construct scope, final String id) {
        this(scope, id, null);

    }

    public AwsVpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        vpc = Vpc.Builder.create(this, "AwsVpc")
                .maxAzs(1)  // Default is all AZs in region
                .build();

    }

    public Vpc getVpc() {
        return vpc;
    }
}
