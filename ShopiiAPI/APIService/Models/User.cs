using System;
using System.Collections.Generic;

namespace APIService.Models;

public partial class User
{
    public Guid Id { get; set; }

    public string Username { get; set; } = null!;

    public string Email { get; set; } = null!;

    public string PasswordHash { get; set; } = null!;

    public string? PhoneNumber { get; set; }

    public Guid? UserAddressId { get; set; }

    public DateTime CreatedAt { get; set; }

    public bool IsActive { get; set; }

    public virtual Address? UserAddress { get; set; }

    public virtual ICollection<Payment> Payments { get; set; } 
}
