cs2031_protocol = Proto("CS2031",  "Alice's Protocol")

packet_type = ProtoField.uint8("cs2031.packet_type", "PacketType", base.DEC)
retain_flag = ProtoField.uint8("cs2031.retain_flag", "RetainFlag", base.DEC)
topic = ProtoField.string("cs2031.topic"     , "Topic"    , base.ASCII)
payload = ProtoField.string("cs2031.payload"     , "Payload"    , base.ASCII)

cs2031_protocol.fields = { packet_type, retain_flag, topic, payload }


function get_type_name(type)
  local type_name = "Unknown"
      if type ==    1 then type_name = "PUBLISH"
  elseif type ==    2 then type_name = "PUBACK"
  elseif type ==    3 then type_name = "SUBSCRIBE"
  elseif type ==    4 then type_name = "SUBACK"
  elseif type ==    5 then type_name = "UNSUBSCRIBE"
  elseif type ==    6 then type_name = "UNSUBACK" end
  return type_name
end

function get_retain_value(retain)
  local retain_value = "Unknown"
      if retain ==    1 then retain_value = "TRUE"
  elseif retain ==    0 then retain_value = "FALSE" end
  return retain_value
end


function cs2031_protocol.dissector(buffer, pinfo, tree)
  length = buffer:len()
  if length == 0 then return end

  pinfo.cols.protocol = cs2031_protocol.name

  local subtree = tree:add(cs2031_protocol, buffer(), "Alice's Protocol Data")

  local type = buffer(0,1):le_uint()
  local type_name = get_type_name(type)

  subtree:add_le(packet_type, buffer(0,1)):append_text(" (" .. type_name .. ")")

  content = buffer(2,length-2):string()
  local index = 1
  local splitContent = {}
  for split in string.gmatch(content, "[^:]+") do
    splitContent[index] = split
    index = index + 1
  end

  if (type_name == "PUBLISH") then
    local retain = buffer(1,1):le_uint()
    local retain_value = get_retain_value(retain)

    subtree:add_le(retain_flag, buffer(1,1)):append_text(" (" .. retain_value .. ")")
  end

  if (type_name == "UNSUBSCRIBE") or (type_name == "SUBSCRIBE") or (type_name == "PUBLISH") then
    subtree:add_le(topic, buffer(2,string.len(splitContent[1])))
  end

  if (type_name == "PUBLISH") then
    subtree:add_le(payload, buffer(string.len(splitContent[1])+3,string.len(splitContent[2])))
  end

end

-- TODO change port to 50000

local udp_port = DissectorTable.get("udp.port")
udp_port:add(50001, cs2031_protocol)
