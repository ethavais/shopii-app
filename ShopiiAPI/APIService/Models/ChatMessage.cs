namespace APIService.Models
{
    public class ChatMessage
    {
        public string Id { get; set; }
        public string UserId { get; set; }
        public string Text { get; set; }
        public string Timestamp { get; set; }
        public string From { get; set; } // "user" hoặc "admin"
        public string Status { get; set; }


    }
}
