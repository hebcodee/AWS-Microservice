package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;


public class AwsServicesStack extends Stack {
    public AwsServicesStack(final Construct scope, final String id, final Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public AwsServicesStack(final Construct scope, final String id, final StackProps props, final Cluster cluster) {
        super(scope, id, props);

        Map<String, String> authentication= new HashMap<>();
        authentication.put("SPRING_DATASOURCE_URL", "jdbc:mysql://" + Fn.importValue("pedidos-db-endpoint") + ":3306/pedidos?createDatabaseIfNotExist=true");
        authentication.put("SPRING_DATASOURCE_USERNAME", "admin");
        authentication.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("pedidos-db-senha"));


        ApplicationLoadBalancedFargateService awsService =ApplicationLoadBalancedFargateService.Builder.create(this, "AwsService")
                .serviceName("aws-service")
                .cluster(cluster)           // Required
                .cpu(256)                   // Default is 256
                .desiredCount(1)            // Default is 1
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromRegistry("hebcodee/pedidos-ms:latest"))
                                .containerPort(8080)
                                .containerName("img-pedidos")
                                .environment(authentication)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "PedidosMsLogGroup")
                                                .logGroupName("PedidosMsLog")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .streamPrefix("PedidosMS")
                                        .build()))
                                .build())
                .memoryLimitMiB(512)       // Default is 512
                .publicLoadBalancer(true)   // Default is true
                .build();

        ScalableTaskCount scalableTarget = awsService.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(1)
                .maxCapacity(3)
                .build());

        scalableTarget.scaleOnCpuUtilization("CpuScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(70)
                .scaleInCooldown(Duration.minutes(3))
                .scaleOutCooldown(Duration.minutes(2))
                .build());
        scalableTarget.scaleOnMemoryUtilization("MemoryScaling", MemoryUtilizationScalingProps.builder()
                .targetUtilizationPercent(65)
                .scaleInCooldown(Duration.minutes(3))
                .scaleOutCooldown(Duration.minutes(2))
                .build());

    }
}
