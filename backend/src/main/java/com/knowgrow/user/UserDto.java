package com.knowgrow.user;

/** Exactly the frontend's User type. */
public record UserDto(String id, String name, String email, String role,
                      String department, String team, String title, String avatar) {}
