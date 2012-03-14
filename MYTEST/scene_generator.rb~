#!/usr/bin/ruby

class SceneGenerator
  def initialize
    @file = File.new('generated.xml', 'w')
    @file.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
  end
  
  def write_tag(tag, open, newline, inline=false, indent=0, attrs={})
    @file.write(
        "#{"\n" if newline}#{"  " * indent}<#{"/" unless open}#{tag}#{attrs.map {|k, v| " #{k}=\"#{v}\""}}#{" /" if inline}>")
  end
  
  def write_tag_and_data(data, indent=0)
    data.each_pair do |k, v|
      write_tag(k, true, true, false, indent)
      @file.write("#{v}")
      write_tag(k, false, false, false)
    end
    
    @file.write("\n")
  end
  
  def close
    @file.write("\n")
    @file.close
  end
end

begin
  gen = SceneGenerator.new
  gen.write_tag('scene', true, false, false)
  
  gen.write_tag_and_data({'image' => '800 600'}, 1)
  
  gen.write_tag('camera', true, true, false, 1)
  gen.write_tag_and_data({
    'viewPoint' => '10 4.8 4',
    'viewDir' => '-5 -2.4 -2',
    'projNormal' => '5 2.4 2',
    'viewUp' => '0 0 1',
    'projDistance' => '4.5',
    'viewWidth' => '8',
    'viewHeight' => '6'},
    2)
  gen.write_tag('camera', false, false, false, 1)
  
  # all lights
  gen.write_tag('light', true, true, false, 1)
  gen.write_tag_and_data({
    'position' => '4 5 -3',
    'intensity' => '1 1 1'
  },
  2)
  gen.write_tag('light', false, false, false, 1)
  
  gen.write_tag('light', true, true, false, 1)
  gen.write_tag_and_data({
    'position' => '-5 -5 6',
    'intensity' => '0.7 0.7 0.3'
  },
  2)
  gen.write_tag('light', false, false, false, 1)
  
 20.times do |count|
	gen.write_tag('light', true, true, false, 1)
	gen.write_tag_and_data({
	'position' => "#{-5+count} #{-5+count} #{6+count/2.0}",
	'intensity' => '0.1 0.1 0.05'
	},
	2)
	gen.write_tag('light', false, false, false, 1)
  end
  
  # all shaders
  gen.write_tag('shader', true, true, false, 1, {'name' => 'blue', 'type' => 'Phong'})
  gen.write_tag_and_data({
    'diffuseColor' => '.2 .3 .8',
    'specularColor' => '1 1 0',
    'exponent' => 10},
  2)
  gen.write_tag('shader', false, false, false, 1)
  
    gen.write_tag('shader', true, true, false, 1, {'name' => 'blueL', 'type' => 'Lambertian'})
  gen.write_tag_and_data({
    'diffuseColor' => '.2 .3 .8'},
  2)
  gen.write_tag('shader', false, false, false, 1)
  
  gen.write_tag('shader', true, true, false, 1, {'name' => 'cone', 'type' => 'Lambertian'})
  gen.write_tag_and_data({
    'diffuseColor' => '1 1 .8'},
  2)
  gen.write_tag('shader', false, false, false, 1)
  
    gen.write_tag('shader', true, true, false, 1, {'name' => 'star', 'type' => 'Phong'})
  gen.write_tag_and_data({
    'diffuseColor' => '.9 1 .9',    
    'specularColor' => '1 1 0.5',
    'exponent' => 20},
  2)
  gen.write_tag('shader', false, false, false, 1)
  
  
  21.times do |count|
	  gen.write_tag('shader', true, true, false, 1, {'name' => "icecream#{count-0}", 'type' => 'Lambertian'})
	  gen.write_tag_and_data({
	    'diffuseColor' => "1 #{1-count/20.0} #{1-count/20.0}"},
	  2)
  	  gen.write_tag('shader', false, false, false, 1)
  end
  
  # Construct the scene
  gen.write_tag('surface', true, true, false, 1, {'type' => 'Sphere'})
  gen.write_tag('shader', true, true, true, 2, {'ref' => 'blue'})
  gen.write_tag_and_data({
      'center' => "#{0} #{0} #{2.5}",
      'radius' => "#{2.5}"
    },
    2)
    gen.write_tag('surface', false, false, false, 1)

 220.times do |count|
    i = count / 25.0
    x = i * Math.cos(75 * i)
    y = i * Math.sin(75 * i)
    z = 1 - Math.sqrt(i)
    if (count < 200) 
	    gen.write_tag('surface', true, true, false, 1, {'type' => 'Box'})
	    gen.write_tag('shader', true, true, true, 2, {'ref' => 'blueL'})
	    gen.write_tag_and_data({
	      'minPt' => "#{x} #{y} #{z}",
	      'maxPt' => "#{x + i / 10.0} #{y + i / 10.0} #{z + i / 10.0}"
	    },
	    2)
	    gen.write_tag('surface', false, false, false, 1)
	    
	    gen.write_tag('surface', true, true, false, 1, {'type' => 'Cylinder'})
	    gen.write_tag('shader', true, true, true, 2, {'ref' => 'blue'})
	    gen.write_tag_and_data({
	      'center' => "#{x+ i / 20.0} #{y + i / 20.0} #{z + i / 10.0 + 0.05}",
	      'radius' => "#{i / 20.0}",
	      'height' => "0.1"
	    },
	    2)
	    gen.write_tag('surface', false, false, false, 1)
	    
    else
	    gen.write_tag('surface', true, true, false, 1, {'type' => 'Cone'})
	    gen.write_tag('shader', true, true, true, 2, {'ref' => 'cone'})
	    gen.write_tag_and_data({
	      'center' => "#{x} #{y} #{z+0.6}",
	      'radius' => "0.4",
	      'height' => "1.2",
	      'tipz' => "#{z}"
	    },
	    2)
	    gen.write_tag('surface', false, false, false, 1)
	    
	    gen.write_tag('surface', true, true, false, 1, {'type' => 'Sphere'})
	    gen.write_tag('shader', true, true, true, 2, {'ref' => "icecream#{count-200}"})
	    gen.write_tag_and_data({
	      'center' => "#{x} #{y} #{z+1.8}",
	      'radius' => "0.99"
	    },
	    2)
	    gen.write_tag('surface', false, false, false, 1)
    end
  end
  
40.times do |count|
    x = rand * 20 - 10
    y = rand * 20 - 10
    z = rand + 5
    r = rand * 0.1 + 0.025
    gen.write_tag('surface', true, true, false, 1, {'type' => 'Sphere'})
    gen.write_tag('shader', true, true, true, 2, {'ref' => 'star'})
    gen.write_tag_and_data({
      'center' => "#{x} #{y} #{z}",
      'radius' => "#{r}"
    },
    2)
    gen.write_tag('surface', false, false, false, 1)
    end
  
  gen.write_tag('scene', false, true, false, 0)
  gen.close
end
