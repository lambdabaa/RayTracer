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
    'viewPoint' => '10 4.8 6',
    'viewDir' => '-5 -2.4 -3',
    'projNormal' => '5 2.4 3',
    'viewUp' => '0 1 0',
    'projDistance' => '6',
    'viewWidth' => '4',
    'viewHeight' => '2.25'},
    2)
  gen.write_tag('camera', false, false, false, 1)
  
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
    'intensity' => '1 1 1'
  },
  2)
  gen.write_tag('light', false, false, false, 1)
  
  gen.write_tag('shader', true, true, false, 1, {'name' => 'blue', 'type' => 'Phong'})
  gen.write_tag_and_data({
    'diffuseColor' => '.2 .3 .8',
    'specularColor' => '1 1 0',
    'exponent' => 20},
  2)
  gen.write_tag('shader', false, false, false, 1)
  
  # Construct the scene
  50.times do |count|
    i = count / 25.0
    x = i * Math.cos(25 * i)
    y = 1 - Math.sqrt(i)
    z = i * Math.sin(25 * i)
    
    gen.write_tag('surface', true, true, false, 1, {'type' => 'Box'})
    gen.write_tag('shader', true, true, true, 2, {'ref' => 'blue'})
    gen.write_tag_and_data({
      'minPt' => "#{x} #{y} #{z}",
      'maxPt' => "#{x + i / 10.0} #{y + i / 10.0} #{z + i / 10.0}"
    },
    2)
    gen.write_tag('surface', false, false, false, 1)
  end
  
  gen.write_tag('scene', false, true, false, 0)
  gen.close
end
