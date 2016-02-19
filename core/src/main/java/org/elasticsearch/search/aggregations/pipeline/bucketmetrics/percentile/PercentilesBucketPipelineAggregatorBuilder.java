/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.BucketMetricsPipelineAggregatorBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PercentilesBucketPipelineAggregatorBuilder
        extends BucketMetricsPipelineAggregatorBuilder<PercentilesBucketPipelineAggregatorBuilder> {

    static final PercentilesBucketPipelineAggregatorBuilder PROTOTYPE = new PercentilesBucketPipelineAggregatorBuilder("", "");

    private double[] percents = new double[] { 1.0, 5.0, 25.0, 50.0, 75.0, 95.0, 99.0 };

    public PercentilesBucketPipelineAggregatorBuilder(String name, String bucketsPath) {
        this(name, new String[] { bucketsPath });
    }

    private PercentilesBucketPipelineAggregatorBuilder(String name, String[] bucketsPaths) {
        super(name, PercentilesBucketPipelineAggregator.TYPE.name(), bucketsPaths);
    }

    /**
     * Get the percentages to calculate percentiles for in this aggregation
     */
    public double[] percents() {
        return percents;
    }

    /**
     * Set the percentages to calculate percentiles for in this aggregation
     */
    public PercentilesBucketPipelineAggregatorBuilder percents(double[] percents) {
        if (percents == null) {
            throw new IllegalArgumentException("[percents] must not be null: [" + name + "]");
        }
        for (Double p : percents) {
            if (p == null || p < 0.0 || p > 100.0) {
                throw new IllegalArgumentException(PercentilesBucketParser.PERCENTS.getPreferredName()
                        + " must only contain non-null doubles from 0.0-100.0 inclusive");
            }
        }
        this.percents = percents;
        return this;
    }

    @Override
    protected PipelineAggregator createInternal(Map<String, Object> metaData) throws IOException {
        return new PercentilesBucketPipelineAggregator(name, percents, bucketsPaths, gapPolicy(), formatter(), metaData);
    }

    @Override
    public void doValidate(AggregatorFactory<?> parent, AggregatorFactory<?>[] aggFactories,
            List<PipelineAggregatorBuilder<?>> pipelineAggregatorFactories) {
        if (bucketsPaths.length != 1) {
            throw new IllegalStateException(PipelineAggregator.Parser.BUCKETS_PATH.getPreferredName()
                    + " must contain a single entry for aggregation [" + name + "]");
        }

        for (Double p : percents) {
            if (p == null || p < 0.0 || p > 100.0) {
                throw new IllegalStateException(PercentilesBucketParser.PERCENTS.getPreferredName()
                        + " must only contain non-null doubles from 0.0-100.0 inclusive");
            }
        }
    }

    @Override
    protected XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        if (percents != null) {
            builder.field(PercentilesBucketParser.PERCENTS.getPreferredName(), percents);
        }
        return builder;
    }

    @Override
    protected PercentilesBucketPipelineAggregatorBuilder innerReadFrom(String name, String[] bucketsPaths, StreamInput in)
            throws IOException {
        PercentilesBucketPipelineAggregatorBuilder factory = new PercentilesBucketPipelineAggregatorBuilder(name, bucketsPaths);
        factory.percents = in.readDoubleArray();
        return factory;
    }

    @Override
    protected void innerWriteTo(StreamOutput out) throws IOException {
        out.writeDoubleArray(percents);
    }

    @Override
    protected int innerHashCode() {
        return Arrays.hashCode(percents);
    }

    @Override
    protected boolean innerEquals(BucketMetricsPipelineAggregatorBuilder<PercentilesBucketPipelineAggregatorBuilder> obj) {
        PercentilesBucketPipelineAggregatorBuilder other = (PercentilesBucketPipelineAggregatorBuilder) obj;
        return Objects.deepEquals(percents, other.percents);
    }

}