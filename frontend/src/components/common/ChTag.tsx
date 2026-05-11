interface ChTagProps {
  channel: string;
  channelColor: string;
}

export default function ChTag({ channel, channelColor }: ChTagProps) {
  return (
    <span className="cht" style={{ borderColor: channelColor + '55', color: channelColor }}>
      {channel}
    </span>
  );
}
