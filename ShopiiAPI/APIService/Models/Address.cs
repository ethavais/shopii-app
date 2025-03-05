using System;
using System.Collections.Generic;

namespace APIService.Models;

public partial class Address
{
    public Guid Id { get; set; }

    public string StreetAddress { get; set; } = null!;

    public string City { get; set; } = null!;

    public virtual ICollection<User> Users { get; set; } = new List<User>();
}
