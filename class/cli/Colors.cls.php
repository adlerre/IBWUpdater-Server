<?php
/**
 * Change the color of text.
 *
 * Reference: http://graphcomp.com/info/specs/ansi_col.html#colors
 */
class Colors {
	static protected $_colors = array(
			'color' => array(
					'black'   => 30,
					'red'	 => 31,
					'green'   => 32,
					'yellow'  => 33,
					'blue'	=> 34,
					'magenta' => 35,
					'cyan'	=> 36,
					'white'   => 37
			),
			'style' => array(
					'bright'	 => 1,
					'dim'		=> 2,
					'underscore' => 4,
					'blink'	  => 5,
					'reverse'	=> 7,
					'hidden'	 => 8
			),
			'background' => array(
					'black'   => 40,
					'red'	 => 41,
					'green'   => 42,
					'yellow'  => 43,
					'blue'	=> 44,
					'magenta' => 45,
					'cyan'	=> 46,
					'white'   => 47
			)
	);

	/**
	 * Set the color.
	 *
	 * @param string  $color  The name of the color or style to set.
	 */
	static public function color($color) {
		if (!is_array($color)) {
			$color = compact('color');
		}

		$color += array('color' => null, 'style' => null, 'background' => null);

		if ($color['color'] == 'reset') {
			return "\033[0m";
		}

		$colors = array();
		foreach (array('color', 'style', 'background') as $type) {
			$code = @$color[$type];
			if (isset(self::$_colors[$type][$code])) {
				$colors[] = self::$_colors[$type][$code];
			}
		}

		if (empty($colors)) {
			$colors[] = 0;
		}

		return "\033[" . join(';', $colors) . "m";
	}

	static public function colorize($string, $colored = true) {
		static $conversions = array(
				'%y' => array('color' => 'yellow'),
				'%g' => array('color' => 'green'),
				'%b' => array('color' => 'blue'),
				'%r' => array('color' => 'red'),
				'%p' => array('color' => 'magenta'),
				'%m' => array('color' => 'magenta'),
				'%c' => array('color' => 'cyan'),
				'%w' => array('color' => 'grey'),
				'%k' => array('color' => 'black'),
				'%n' => array('color' => 'reset'),
				'%Y' => array('color' => 'yellow', 'style' => 'bright'),
				'%G' => array('color' => 'green', 'style' => 'bright'),
				'%B' => array('color' => 'blue', 'style' => 'bright'),
				'%R' => array('color' => 'red', 'style' => 'bright'),
				'%P' => array('color' => 'magenta', 'style' => 'bright'),
				'%M' => array('color' => 'magenta', 'style' => 'bright'),
				'%C' => array('color' => 'cyan', 'style' => 'bright'),
				'%W' => array('color' => 'grey', 'style' => 'bright'),
				'%K' => array('color' => 'black', 'style' => 'bright'),
				'%N' => array('color' => 'reset', 'style' => 'bright'),
				'%3' => array('background' => 'yellow'),
				'%2' => array('background' => 'green'),
				'%4' => array('background' => 'blue'),
				'%1' => array('background' => 'red'),
				'%5' => array('background' => 'magenta'),
				'%6' => array('background' => 'cyan'),
				'%7' => array('background' => 'grey'),
				'%0' => array('background' => 'black'),
				'%F' => array('style' => 'blink'),
				'%U' => array('style' => 'underline'),
				'%8' => array('style' => 'inverse'),
				'%9' => array('style' => 'bright'),
				'%_' => array('style' => 'bright')
		);

		if (!$colored) {
			return preg_replace('/%((%)|.)/', '$2', $string);
		}

		$string = str_replace('%%', '% ', $string);

		foreach ($conversions as $key => $value) {
			$string = str_replace($key, self::color($value), $string);
		}

		return str_replace('% ', '%', $string);
	}

	/**
	 * Return the length of the string without color codes.
	 *
	 * @param string  $string  the string to measure
	 */
	static public function length($string) {
		return mb_strlen(self::colorize(utf8_decode($string), false));
	}

	/**
	 * Pad the string to a certain display length.
	 *
	 * @param string  $string  the string to pad
	 * @param integer  $length  the display length
	 */
	static public function pad($string, $length) {
		$real_length = mb_strlen($string);
		$show_length = self::length($string);
		$length  += $real_length - $show_length;

		return str_pad($string, $length);
	}
}