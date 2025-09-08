<?php

use PHPUnit\Framework\TestCase;
require __DIR__ . '/../server/AreaChecker.php';

class AreaTest extends TestCase {

    public static function providerForAreaCheck() {
        return [
            // Points in the first quadrant (rectangle check)
            'First Quadrant Inside Rectangle' => [0.5, 0.5, 2, true],
            'First Quadrant Outside Rectangle' => [1.1, 0.5, 2, false],
            'First Quadrant Outside Rectangle Y' => [0.5, 2.1, 2, false],
            
            // Points in the fourth quadrant (triangle check)
            'Fourth Quadrant Inside Triangle' => [0.5, -0.2, 2, true],
            'Fourth Quadrant Outside Triangle' => [2.1, -0.5, 2, false],
            'Fourth Quadrant Outside Triangle Y' => [0.5, -1.1, 2, false],
            
            // Points in the third quadrant (circle check)
            'Third Quadrant Inside Circle' => [-0.5, -0.5, 2, true],
            'Third Quadrant Outside Circle' => [-1.5, -1.5, 2, false],
            
            // Points in the second quadrant (always outside)
            'Second Quadrant Always Outside' => [-0.5, 0.5, 2, false]
        ];        
    }

    /**
     * @dataProvider providerForAreaCheck
     */
    public function testIsInArea($x, $y, $r, $expected) {
        $this->assertSame($expected, AreaChecker::isInArea($x, $y, $r));
    }
}
