uniform mat4 projection;
uniform mat4 modelview;

attribute vec4 position;
attribute vec4 color;
attribute vec2 offset;

attribute float intensity;

varying vec4 vertColor;

uniform float pointScale;
uniform vec4 pointColor;

void main() {
	vec4 pt = position;

	// scale points
	pt /= pointScale + (color.r * 2.0);

	// change color
	vec4 c = pointColor;

	// apply view matrix
	vec4 pos = modelview * pt;
	vec4 clip = projection * pos;
	vec4 clipped = clip + projection * vec4(offset, 0.0, 0.0);

	gl_Position = clipped;
	vertColor = c;
}
