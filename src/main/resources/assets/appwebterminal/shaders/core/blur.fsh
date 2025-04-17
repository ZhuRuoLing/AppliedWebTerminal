#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 BlurDir;
uniform float weight[7] = float[] (0.207027, 0.1519810, 0.114514, 0.114514, 0.016216, 0.0111, 0.0100);

in vec2 texCoord;

out vec4 fragColor;

float apply(float f) {
    return -(f - 1) * (f - 1) + 1;
}

void main() {
    vec2 texOffset = 1.0 / textureSize(DiffuseSampler, 0);

    vec3 result = texture(DiffuseSampler, texCoord).rgb * weight[0];
    for (int i = 1; i < 7; ++i) {
        result += texture(DiffuseSampler, texCoord + vec2(BlurDir.x * texOffset.x * i * 1.943, BlurDir.y * texOffset.y * i * 1.943)).rgb * weight[i];
        result += texture(DiffuseSampler, texCoord - vec2(BlurDir.x * texOffset.x * i * 1.943, BlurDir.y * texOffset.y * i * 1.943)).rgb * weight[i];
    }
    vec3 color = vec3(apply(result.r), apply(result.g), apply(result.b));
    fragColor = vec4(result.rgb * 0.95, 1.0);
}