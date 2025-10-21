// spotlight_darken.frag
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;        // The texture containing the entire rendered scene
uniform vec2 u_resolution;          // Screen resolution (width, height)
uniform vec2 u_center;              // Center of the spotlight in screen coordinates (pixels)
uniform float u_radius;             // Radius of the bright area (pixels)
uniform float u_falloff;            // How smooth the transition is (smaller = sharper, larger = smoother)
uniform float u_darkenAmount;       // How much to darken outside the radius (e.g., 0.5 for 50%)

varying vec2 v_texCoords;

void main() {
    // Sample the original scene color
    vec4 sceneColor = texture2D(u_texture, v_texCoords);

    // Convert normalized texture coordinates (0.0-1.0) to screen pixel coordinates
    vec2 pixelCoords = v_texCoords * u_resolution;

    // Calculate distance from the current pixel to the spotlight center
    float dist = distance(pixelCoords, u_center);

    // Calculate the 'lightness factor' for this pixel
    // This will be 1.0 at the center, falling off to 0.0 outside the radius.
    float lightnessFactor = 1.0 - smoothstep(u_radius, u_radius + u_falloff, dist);

    // Apply the darkening effect.
    // Outside the spotlight: sceneColor.rgb * (1.0 - u_darkenAmount)
    // Inside the spotlight: sceneColor.rgb
    // The lightnessFactor interpolates between these two states.
    vec3 finalColor = mix(sceneColor.rgb * (1.0 - u_darkenAmount), sceneColor.rgb, lightnessFactor);

    gl_FragColor = vec4(finalColor, sceneColor.a);
}
