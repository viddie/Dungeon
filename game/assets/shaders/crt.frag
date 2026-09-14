#ifdef GL_ES
precision mediump float;
#endif

// *****IMPORT: util.glsl*****

// ----- From vertex shader -----
varying vec2 uv;

// ----- From LibGDX -----
uniform sampler2D u_texture;

// ----- Common uniforms set by DrawSystem -----
uniform vec2 u_resolution;
uniform float u_time;
uniform vec2 u_mouse;
uniform vec2 u_texelSize;
uniform vec2 u_aspect;
uniform vec4 u_entityBounds;

// ----- Custom uniforms -----
uniform bool u_vertical;
uniform float u_lineWidth;
uniform float u_warpStrength;

vec2 warpUv(vec2 inputUv) {
  vec2 centered = inputUv * 2.0 - 1.0;
  float radiusSquared = dot(centered, centered);
  float bulge = 1.0 + u_warpStrength * 0.12 * radiusSquared;
  return centered * bulge * 0.5 + 0.5;
}

void main() {
  vec2 centeredUv = uv * 2.0 - 1.0;
  vec2 origUv = uv;
  uv = warpUv(uv);
  vec4 color = vec4(0.0);

  if (uv.x >= 0.0 && uv.x <= 1.0 && uv.y >= 0.0 && uv.y <= 1.0) {
    color = unPma(texture2D(u_texture, uv));
  }

  float lineWidth = max(u_lineWidth, 0.001);
  float lineAxis = u_vertical ? uv.x : uv.y;
  float lineResolution = u_vertical ? u_resolution.x : u_resolution.y;
  float linePosition = fract(lineAxis * lineResolution / (lineWidth * 2.0));
  float darkLine = step(0.5, linePosition);
  float t1 = (sin(u_time * TAU * 0.5 + PI * 0.3) + 1) * 0.02;
  float scanline = mix(1.1 + t1, 0.72 - t1, darkLine);

  float distanceFromCenter = dot(centeredUv, centeredUv);
  float t2 = sin(u_time * TAU * 0.25) * 0.25;
  float vignette = 1.0 - 0.5 * smoothstep(0.45 + t2, 1.3, distanceFromCenter);

  color.rgb *= scanline * vignette;
  gl_FragColor = pma(color);
}
