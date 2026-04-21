package com.blue.zelda.code.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zelda.code")
public class CodeGeneratorProperties {

    private boolean enabled = true;
    private int seqLength = 4;
    private int threshold = 9000;
    private int maxNode = 10;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getSeqLength() { return seqLength; }
    public void setSeqLength(int seqLength) { this.seqLength = seqLength; }
    public int getThreshold() { return threshold; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
    public int getMaxNode() { return maxNode; }
    public void setMaxNode(int maxNode) { this.maxNode = maxNode; }
}
