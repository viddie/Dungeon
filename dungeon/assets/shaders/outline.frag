#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform vec4 u_outlineColor;
// Changed u_outlineSize to an int for clarity in loops,
// though float works with proper casting/loop range.
uniform float u_outlineSize;

varying vec2 v_texCoords;

void main() {
    vec4 baseColor = texture2D(u_texture, v_texCoords);

    // 1. Draw the sprite body if the pixel is opaque
    if (baseColor.a > 0.0) {
        gl_FragColor = baseColor;
        return;
    }

    // 2. Check for the outline ONLY if the current pixel is transparent.

    // We only need to search up to u_outlineSize texels away.
    // Use an integer size for the loops.
    int size = int(u_outlineSize);
    float max_alpha = 0.0;

    // Check surrounding pixels up to the outline size
    for (int x = -size; x <= size; x++) {
        for (int y = -size; y <= size; y++) {
            // Skip the current center pixel (x=0, y=0) - already checked/discarded
            if (x == 0 && y == 0) continue;

            // Only check up to the maximum required distance
            if (float(x*x + y*y) > u_outlineSize * u_outlineSize) continue;

            vec2 offset = vec2(float(x), float(y)) * u_texelSize;
            max_alpha = max(max_alpha, texture2D(u_texture, v_texCoords + offset).a);
        }
    }

    // 3. Apply the outline
    if (max_alpha > 0.0) {
        // Use the alpha of the nearest opaque pixel to feather the outline
        // if the original sprite had transparent edges (premultiplied alpha)
        gl_FragColor = vec4(u_outlineColor.rgb, u_outlineColor.a * max_alpha);
    } else {
        // Nothing found, discard this fragment
        discard;
    }
}
